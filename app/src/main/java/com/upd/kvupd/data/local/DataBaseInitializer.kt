package com.upd.kvupd.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.upd.kvupd.utils.BaseDatosRoom.DB_NAME
import com.upd.kvupd.utils.BaseDatosRoom.VERSION_BASEDATOS
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_ROOM
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DataBaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: SharedPreferences
) {
    fun build(): TablesRoom {
        val oldVersion = preferences.getInt(KEY_ROOM, -1)

        val existeVersionAnterior = oldVersion in 1 until CURRENT_VERSION

        if (existeVersionAnterior) {
            // Metodo para salvar informacion de datos importantes en csv
            // Subir los datos salvados a traves de un worker
        }

        val db = Room.databaseBuilder(context, TablesRoom::class.java, DB_NAME)
            .fallbackToDestructiveMigration(true)
            .build()

        // Guarda nueva versión si la DB se construyo bien
        preferences.edit().putInt(KEY_ROOM, CURRENT_VERSION).apply()

        return db
    }

    companion object {
        const val CURRENT_VERSION = VERSION_BASEDATOS
    }
}