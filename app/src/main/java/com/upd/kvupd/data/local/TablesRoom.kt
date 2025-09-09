package com.upd.kvupd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaAuxiliar
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableAltaFoto
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaEstado
import com.upd.kvupd.data.model.TableBajaSupervisor
import com.upd.kvupd.data.model.TableCliente
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.TableConsulta
import com.upd.kvupd.data.model.TableDistrito
import com.upd.kvupd.data.model.TableEncuesta
import com.upd.kvupd.data.model.TableEstado
import com.upd.kvupd.data.model.TableIncidencia
import com.upd.kvupd.data.model.TableNegocio
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.model.TableRuta
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.data.model.TableSeleccionEncuesta
import com.upd.kvupd.data.model.TableVendedor
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_BASEDATOS

@Database(
    version = VERSION_BASEDATOS,
    entities = [
        TableConfiguracion::class, TableCliente::class, TableVendedor::class, TableDistrito::class,
        TableNegocio::class, TableEncuesta::class, TableSeleccionEncuesta::class,
        TableRespuesta::class, TableRuta::class, TableEstado::class, TableSeguimiento::class,
        TableBaja::class, TableAlta::class, TableAltaDatos::class,
        TableAltaFoto::class, TableBajaSupervisor::class, TableBajaEstado::class,
        TableIncidencia::class, TableAltaAuxiliar::class, TableConsulta::class
    ],
    exportSchema = true // Se debe crear una ruta para el esquema en build.gradle
)

abstract class TablesRoom : RoomDatabase() {
    abstract fun getCrudDao(): Crud
    abstract fun getQueryDao(): QueryList
}