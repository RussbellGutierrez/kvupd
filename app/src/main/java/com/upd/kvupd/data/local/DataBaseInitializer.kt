package com.upd.kvupd.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.room.Room
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
import java.io.FileWriter
import javax.inject.Inject

class DataBaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context
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
                exportPendientes()

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

    private fun exportPendientes() {
        openOldCoreDb().use { db ->

            exportCsv(db, "TableSeguimiento", "${PREFIJO_CSV}seguimiento.csv")
            exportCsv(db, "TableAlta", "${PREFIJO_CSV}alta.csv")
            exportCsv(db, "TableAltaDatos", "${PREFIJO_CSV}altadatos.csv")
            exportCsv(db, "TableBaja", "${PREFIJO_CSV}baja.csv")
            exportCsv(db, "TableBajaProcesada", "${PREFIJO_CSV}bajaprocesada.csv")
            exportCsv(db, "TableRespuesta", "${PREFIJO_CSV}respuesta.csv")
            exportCsv(db, "TableFoto", "${PREFIJO_CSV}foto.csv")
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

    private fun exportCsv(db: SQLiteDatabase, table: String, fileName: String) {

        val folder = File(context.filesDir, FOLDER_CORE)

        if (!folder.exists()) folder.mkdirs()

        val file = File(folder, fileName)

        db.rawQuery(
            "SELECT * FROM $table WHERE sincronizado = 0",
            null
        ).use { c ->

            if (c.count == 0) return@use

            FileWriter(file).use { writer ->

                val cols = c.columnNames

                writer.appendLine(
                    cols.joinToString(SEPARADOR)
                )

                while (c.moveToNext()) {

                    val row = cols.joinToString(SEPARADOR) { col ->

                        val value =
                            c.getString(
                                c.getColumnIndexOrThrow(col)
                            ) ?: ""

                        "\"${value.replace("\"", "\"\"")}\""
                    }

                    writer.appendLine(row)
                }
            }
        }
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