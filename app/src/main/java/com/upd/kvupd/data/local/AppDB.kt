package com.upd.kvupd.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upd.kvupd.data.model.*

@Database(
    version = 1,
    entities = [TSesion::class, TConfiguracion::class, TClientes::class, TEmpleados::class, TDistrito::class,
        TNegocio::class, TRutas::class, TEncuesta::class, TRespuesta::class, TEstado::class, TSeguimiento::class,
        TVisita::class, TBaja::class, TAlta::class, TADatos::class, TBajaSuper::class, TBEstado::class, TEncuestaSeleccionado::class],
    exportSchema = true
)

/** autoMigrations = [AutoMigration (from = 1, to = 2)]
 *
sirve para migrar tablas room
se debe agregar una ruta para el archivo schema en build.gradle*/
abstract class AppDB : RoomDatabase() {
    abstract fun getDao(): AppDAO
    abstract fun getQDao(): QueryDAO
}