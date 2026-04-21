package com.upd.kvupd.data.local.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_CORE

@Database(
    version = VERSION_CORE,
    entities = [
        TableConfiguracion::class,
        TableSeguimiento::class,
        TableAlta::class,
        TableAltaDatos::class,
        TableBaja::class,
        TableBajaProcesada::class,
        TableRespuesta::class,
        TableFoto::class
    ],
    exportSchema = true
)
abstract class CoreRoom : RoomDatabase() {
    abstract fun getCrudDao(): CoreCrud
    abstract fun getQueryDao(): CoreQuery
}