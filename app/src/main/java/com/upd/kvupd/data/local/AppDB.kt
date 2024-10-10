package com.upd.kvupd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upd.kvupd.data.model.TAAux
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAFoto
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.data.model.TClientes
import com.upd.kvupd.data.model.TConfiguracion
import com.upd.kvupd.data.model.TConsulta
import com.upd.kvupd.data.model.TDistrito
import com.upd.kvupd.data.model.TEmpleados
import com.upd.kvupd.data.model.TEncuesta
import com.upd.kvupd.data.model.TEncuestaSeleccionado
import com.upd.kvupd.data.model.TEstado
import com.upd.kvupd.data.model.TIncidencia
import com.upd.kvupd.data.model.TNegocio
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.data.model.TSesion
import com.upd.kvupd.data.model.TVisita

@Database(
    version = 13,
    entities = [TSesion::class, TConfiguracion::class, TClientes::class, TEmpleados::class, TDistrito::class,
        TNegocio::class, TRutas::class, TEncuesta::class, TRespuesta::class, TEstado::class, TSeguimiento::class,
        TVisita::class, TBaja::class, TAlta::class, TADatos::class, TBajaSuper::class, TBEstado::class,
        TEncuestaSeleccionado::class, TIncidencia::class, TAFoto::class, TAAux::class, TConsulta::class],
    exportSchema = true
)

/** autoMigrations = [AutoMigration (from = 1, to = 2)]
 *No es necesario en caso se use la opcion de migracion destructiva
sirve para migrar tablas room
se debe agregar una ruta para el archivo schema en build.gradle*/

abstract class AppDB : RoomDatabase() {
    abstract fun getDao(): AppDAO
    abstract fun getQDao(): QueryDAO
}