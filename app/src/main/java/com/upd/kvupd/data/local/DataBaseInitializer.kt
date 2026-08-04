package com.upd.kvupd.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.util.AtomicFile
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.upd.kvupd.data.local.cache.CacheRoom
import com.upd.kvupd.data.local.core.CoreRoom
import com.upd.kvupd.data.local.enumClass.CoreUpdateStage
import com.upd.kvupd.data.local.localClass.CoreUpdateException
import com.upd.kvupd.data.local.modelbd.CoreBackup
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.utils.BaseDatosRoom.CACHE_NAME
import com.upd.kvupd.utils.BaseDatosRoom.CORE_NAME
import com.upd.kvupd.utils.BaseDatosRoom.DATABASE_NOT_FOUND
import com.upd.kvupd.utils.BaseDatosRoom.FOLDER_CORE
import com.upd.kvupd.utils.BaseDatosRoom.PREFIJO_CSV
import com.upd.kvupd.utils.BaseDatosRoom.SEPARADOR
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_CORE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject

class DataBaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val operationSource: OperationSource
) {
    private val _tag by lazy { DataBaseInitializer::class.java.simpleName }

    fun buildCore(): CoreRoom {

        var stage = CoreUpdateStage.READ_VERSION
        var installedVersion = DATABASE_NOT_FOUND
        var newDatabase: CoreRoom? = null

        try {
            installedVersion = getDatabaseVersion(CORE_NAME)

            stage = CoreUpdateStage.VALIDATE_VERSION

            if (
                installedVersion != DATABASE_NOT_FOUND &&
                installedVersion > VERSION_CORE
            ) {
                throw IllegalStateException(
                    "Downgrade de CoreRoom no soportado: " +
                            "$installedVersion -> $VERSION_CORE"
                )
            }

            val cambioVersion =
                installedVersion != DATABASE_NOT_FOUND &&
                        installedVersion < VERSION_CORE

            var backup: CoreBackup? = null

            if (cambioVersion) {
                Log.i(
                    _tag,
                    "Actualizando CoreRoom: $installedVersion -> $VERSION_CORE"
                )

                stage = CoreUpdateStage.BACKUP_CONFIGURATION
                backup = backupCoreData()

                stage = CoreUpdateStage.EXPORT_PENDING
                exportPendientes(installedVersion)

                stage = CoreUpdateStage.DELETE_OLD_DATABASE

                check(deleteCoreDatabase()) {
                    "No se pudo eliminar la base de datos antigua: $CORE_NAME"
                }
            }

            stage = CoreUpdateStage.CREATE_NEW_DATABASE

            newDatabase = Room.databaseBuilder(
                context,
                CoreRoom::class.java,
                CORE_NAME
            ).build()

            backup?.let {
                stage = CoreUpdateStage.RESTORE_CONFIGURATION
                restoreCoreData(newDatabase, it)
            }

            if (cambioVersion) {
                limpiarArchivosCoreObsoletos()
            }

            return newDatabase

        } catch (error: Exception) {
            newDatabase?.close()

            val updateError = CoreUpdateException(
                stage = stage,
                installedVersion = installedVersion,
                targetVersion = VERSION_CORE,
                cause = error
            )

            reportCoreUpdateFailure(updateError)

            throw updateError
        }
    }

    fun buildCache(): CacheRoom {
        return Room.databaseBuilder(
            context,
            CacheRoom::class.java,
            CACHE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(
                object : RoomDatabase.Callback() {

                    override fun onDestructiveMigration(
                        db: SupportSQLiteDatabase
                    ) {
                        super.onDestructiveMigration(db)
                        operationSource.lanzarRecuperacionCache()
                    }
                }
            )
            .build()
    }

    /**
     * Obtiene la versión real almacenada por SQLite en PRAGMA user_version.
     *
     * Retorna DATABASE_NOT_FOUND cuando el archivo todavía no existe,
     * como sucede en una instalación nueva.
     */
    private fun getDatabaseVersion(databaseName: String): Int {

        val databaseFile = context.getDatabasePath(databaseName)

        if (!databaseFile.exists()) {
            return DATABASE_NOT_FOUND
        }

        return SQLiteDatabase.openDatabase(
            databaseFile.path,
            null,
            SQLiteDatabase.OPEN_READONLY
        ).use { database ->
            database.version
        }
    }

    private fun backupCoreData(): CoreBackup =
        openOldCoreDb().use { db ->

            val config = readConfiguracion(db)
            CoreBackup(config)
        }

    private fun limpiarArchivosCoreObsoletos() {
        val folder = File(context.filesDir, FOLDER_CORE)

        if (!folder.isDirectory) {
            return
        }

        folder.listFiles()
            .orEmpty()
            .forEach { file ->

                val debeEliminar = when {
                    file.name.endsWith(".failed") -> true
                    file.name.endsWith(".invalid") -> true

                    file.name.endsWith(".retry") -> {
                        val csvName = file.name.removeSuffix(".retry")
                        !File(folder, csvName).exists()
                    }

                    else -> false
                }

                if (debeEliminar && file.exists() && !file.delete()) {
                    Log.w(
                        _tag,
                        "No se pudo eliminar el archivo obsoleto: ${file.name}"
                    )
                }
            }
    }

    private fun exportPendientes(installedVersion: Int) {
        val prefix = "${PREFIJO_CSV}${installedVersion}_"

        openOldCoreDb().use { db ->
            exportCsv(db, "TableSeguimiento", "${prefix}seguimiento.csv")
            exportCsv(db, "TableAlta", "${prefix}alta.csv")
            exportCsv(db, "TableAltaDatos", "${prefix}altadatos.csv")
            exportCsv(db, "TableBaja", "${prefix}baja.csv")
            exportCsv(db, "TableBajaProcesada", "${prefix}bajaprocesada.csv")
            exportCsv(db, "TableRespuesta", "${prefix}respuesta.csv")
            exportCsv(db, "TableFoto", "${prefix}foto.csv")
        }
    }

    private fun deleteCoreDatabase(): Boolean {
        return context.deleteDatabase(CORE_NAME)
    }

    private fun restoreCoreData(
        db: CoreRoom,
        backup: CoreBackup
    ) {
        backup.configuracion?.let { config ->
            runBlocking {
                db.getCrudDao()
                    .insertConfiguracion(listOf(config))
            }
        }
    }

    private fun openOldCoreDb(): SQLiteDatabase {
        val path = context.getDatabasePath(CORE_NAME).path

        return SQLiteDatabase.openDatabase(
            path,
            null,
            SQLiteDatabase.OPEN_READONLY
        )
    }

    private fun readConfiguracion(
        db: SQLiteDatabase
    ): TableConfiguracion? {

        return db.rawQuery(
            "SELECT * FROM TableConfiguracion LIMIT 1",
            null
        ).use { cursor ->

            if (!cursor.moveToFirst()) {
                return@use null
            }

            TableConfiguracion(
                codigo = cursor.getString(cursor.getColumnIndexOrThrow("codigo")),
                empresa = cursor.getInt(cursor.getColumnIndexOrThrow("empresa")),
                esquema = cursor.getInt(cursor.getColumnIndexOrThrow("esquema")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                codsuper = cursor.getInt(cursor.getColumnIndexOrThrow("codsuper")),
                supervisor = cursor.getString(cursor.getColumnIndexOrThrow("supervisor")),
                horafin = cursor.getString(cursor.getColumnIndexOrThrow("horafin")),
                horainicio = cursor.getString(cursor.getColumnIndexOrThrow("horainicio")),
                ipp = cursor.getString(cursor.getColumnIndexOrThrow("ipp")),
                ips = cursor.getString(cursor.getColumnIndexOrThrow("ips")),
                seguimiento = cursor.getInt(cursor.getColumnIndexOrThrow("seguimiento")),
                sucursal = cursor.getInt(cursor.getColumnIndexOrThrow("sucursal")),
                tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
            )
        }
    }

    private fun exportCsv(
        db: SQLiteDatabase,
        table: String,
        fileName: String
    ) {
        val folder = File(context.filesDir, FOLDER_CORE)

        check(folder.exists() || folder.mkdirs()) {
            "No se pudo crear la carpeta de respaldo: ${folder.path}"
        }

        val finalFile = File(folder, fileName)

        db.rawQuery(
            "SELECT * FROM $table WHERE sincronizado = 0",
            null
        ).use { cursor ->

            val expectedRows = cursor.count

            if (expectedRows == 0) {
                return
            }

            val columns = cursor.columnNames
            val expectedHeader = columns.joinToString(SEPARADOR)

            if (
                finalFile.exists() &&
                isValidCsv(
                    file = finalFile,
                    expectedHeader = expectedHeader,
                    expectedRows = expectedRows
                )
            ) {
                return
            }

            val atomicFile = AtomicFile(finalFile)
            var output: FileOutputStream? = null

            try {
                output = atomicFile.startWrite()

                val writer = OutputStreamWriter(
                    output,
                    Charsets.UTF_8
                ).buffered()

                writer.appendLine(expectedHeader)

                var writtenRows = 0

                while (cursor.moveToNext()) {
                    val row = columns.joinToString(SEPARADOR) { column ->
                        val value = cursor.getString(
                            cursor.getColumnIndexOrThrow(column)
                        ).orEmpty()

                        val normalizedValue = value
                            .replace("\r\n", " ")
                            .replace('\r', ' ')
                            .replace('\n', ' ')

                        "\"${normalizedValue.replace("\"", "\"\"")}\""
                    }

                    writer.appendLine(row)
                    writtenRows++
                }

                check(writtenRows == expectedRows) {
                    "Exportación incompleta de $table: " +
                            "$writtenRows de $expectedRows registros"
                }

                writer.flush()
                atomicFile.finishWrite(output)
                output = null

            } catch (error: Exception) {
                output?.let(atomicFile::failWrite)
                throw error
            }

            check(
                isValidCsv(
                    file = finalFile,
                    expectedHeader = expectedHeader,
                    expectedRows = expectedRows
                )
            ) {
                "El respaldo final de $table no superó la validación"
            }
        }
    }

    private fun isValidCsv(
        file: File,
        expectedHeader: String,
        expectedRows: Int
    ): Boolean {
        if (!file.isFile || file.length() == 0L) {
            return false
        }

        return runCatching {
            file.bufferedReader(Charsets.UTF_8).use { reader ->
                val header = reader.readLine()
                val rows = reader.lineSequence().count()

                header == expectedHeader &&
                        rows == expectedRows
            }
        }.getOrDefault(false)
    }

    private fun reportCoreUpdateFailure(
        error: CoreUpdateException
    ) {
        Log.e(
            _tag,
            "Falló CoreRoom en ${error.stage}",
            error
        )

        runCatching {
            FirebaseCrashlytics.getInstance().apply {
                setCustomKey("operation", "core_room_update")
                setCustomKey("stage", error.stage.name)
                setCustomKey(
                    "installed_version",
                    error.installedVersion
                )
                setCustomKey(
                    "target_version",
                    error.targetVersion
                )
                log(
                    "Falló la actualización de CoreRoom " +
                            "en ${error.stage}"
                )

                recordException(error)
            }
        }.onFailure { reportError ->
            Log.e(
                _tag,
                "No se pudo agregar contexto a Crashlytics",
                reportError
            )
        }
    }
}