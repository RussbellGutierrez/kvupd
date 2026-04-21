package com.upd.kvupd.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.upd.kvupd.data.local.cache.CacheRoom
import com.upd.kvupd.data.local.core.CoreRoom
import com.upd.kvupd.utils.BaseDatosRoom.CACHE_NAME
import com.upd.kvupd.utils.BaseDatosRoom.CORE_NAME
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_CACHE
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_CORE
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_ROOM_CACHE
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_ROOM_CORE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DataBaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences
) {

    fun buildCore(): CoreRoom {

        val oldVersion = preferences.getInt(KEY_ROOM_CORE, -1)

        val cambioVersion =
            oldVersion != -1 && oldVersion < VERSION_CORE

        if (cambioVersion) {
            // backup config
            // export pendientes
            // restore luego
        }

        val db = Room.databaseBuilder(context, CoreRoom::class.java, CORE_NAME).build()

        preferences.edit()
            .putInt(KEY_ROOM_CORE, VERSION_CORE)
            .apply()

        return db
    }

    fun buildCache(): CacheRoom {

        val db = Room.databaseBuilder(context, CacheRoom::class.java, CACHE_NAME)
            .fallbackToDestructiveMigration(true)
            .build()

        preferences.edit()
            .putInt(KEY_ROOM_CACHE, VERSION_CACHE)
            .apply()

        return db
    }
}