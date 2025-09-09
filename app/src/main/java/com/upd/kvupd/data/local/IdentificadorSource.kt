package com.upd.kvupd.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import com.upd.kvupd.utils.ExtraInfo
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.FirebaseKeys.NODO_FABRICANTE
import com.upd.kvupd.utils.FirebaseKeys.NODO_FECHAHORA
import com.upd.kvupd.utils.FirebaseKeys.NODO_MODELO
import com.upd.kvupd.utils.FirebaseKeys.NODO_UUID
import com.upd.kvupd.utils.SharedPreferenceKeys
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

class IdentificadorSource @Inject constructor(
    private val preferences: SharedPreferences
) {
    fun crearUUID(): String = UUID.randomUUID().toString()

    fun existeUUID(): Boolean = preferences.contains(SharedPreferenceKeys.KEY_UID)

    fun guardarSharedKey(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun obtenerSharedKey(key: String): String? = preferences.getString(key, null)

    fun crearNodoFirebase(uuid: String): Map<String, String> {
        return mapOf(
            NODO_UUID to uuid,
            NODO_MODELO to ExtraInfo.obtener(InfoDispositivo.MODELO),
            NODO_FABRICANTE to ExtraInfo.obtener(InfoDispositivo.FABRICANTE),
            NODO_FECHAHORA to FechaHoraUtil.ahora()
        )
    }

    @SuppressLint("HardwareIds")
    fun crearHashHibrido(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: UUID.randomUUID().toString()

        val fabricante = ExtraInfo.obtener(InfoDispositivo.FABRICANTE)
        val modelo = ExtraInfo.obtener(InfoDispositivo.MODELO)
        val data = "$androidId-$fabricante-$modelo"

        // Convertir a hash SHA-256
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))

        return hashBytes.joinToString("") { "%02x".format(it) } // Hex string
    }
}