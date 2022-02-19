package com.upd.kv.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upd.kv.data.model.*

@Database(
    entities = [TConfiguracion::class, TClientes::class, TEmpleados::class, TDistrito::class, TNegocio::class, TEncuesta::class,
        TEstado::class, TSeguimiento::class, TVisita::class],
    version = 1,
    exportSchema = false
)
abstract class AppDB : RoomDatabase() {
    abstract fun getDao(): AppDAO
    abstract fun getQDao(): QueryDAO
}