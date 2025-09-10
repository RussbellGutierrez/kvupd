package com.upd.kvupd.domain

import android.content.Context
import com.upd.kvupd.data.local.IdentificadorSource
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_UID
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class IdentityImplementation @Inject constructor(
    @ApplicationContext private val context: Context,
    private val androidSource: IdentificadorSource
) : IdentityFunctions {
    override fun obtenerIdentificador(): String? {
        return androidSource.obtenerSharedKey(KEY_UID)
    }

    override fun existeIdentificador(): Boolean {
        return androidSource.existeUUID()
    }

    override fun crearIdentificador(): String {
        return androidSource.crearUUID()
    }

    override fun guardarIdentificador(uuid: String) {
        androidSource.guardarSharedKey(KEY_UID, uuid)
    }

    override fun crearHash(): String {
        return androidSource.crearHashHibrido(context)
    }

    override fun obtenerNodoDatos(uuid: String): Map<String, String> {
        return androidSource.crearNodoFirebase(uuid)
    }
}