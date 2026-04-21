package com.upd.kvupd.data.local.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.upd.kvupd.data.model.cache.TableBajaSupervisor
import com.upd.kvupd.data.model.cache.TableCliente
import com.upd.kvupd.data.model.cache.TableDistrito
import com.upd.kvupd.data.model.cache.TableEncuesta
import com.upd.kvupd.data.model.cache.TableNegocio
import com.upd.kvupd.data.model.cache.TableRuta
import com.upd.kvupd.data.model.cache.TableVendedor
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_CACHE

@Database(
    version = VERSION_CACHE,
    entities = [
        TableCliente::class,
        TableVendedor::class,
        TableDistrito::class,
        TableNegocio::class,
        TableEncuesta::class,
        TableRuta::class,
        TableBajaSupervisor::class
    ],
    exportSchema = true
)
abstract class CacheRoom : RoomDatabase() {
    abstract fun getCrudDao(): CacheCrud
    abstract fun getQueryDao(): CacheQuery
}