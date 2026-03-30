package com.upd.kvupd.data.remote

import com.upd.kvupd.utils.SocketReporte.PATH_ORIUNDA
import com.upd.kvupd.utils.SocketReporte.PATH_TERRANORTE
import com.upd.kvupd.utils.SocketReporte.PORT_ORIUNDA
import com.upd.kvupd.utils.SocketReporte.PORT_TERRANORTE
import javax.inject.Inject

class SocketBuilder @Inject constructor(
    private val firebaseHelper: FirebaseHelper
) {
    suspend fun createUrl(empresa: Int): String {
        val ip = firebaseHelper.obtenerIpFirebase()
        val (port, path) = when (empresa) {
            1 -> PORT_ORIUNDA to PATH_ORIUNDA
            else -> PORT_TERRANORTE to PATH_TERRANORTE
        }
        return "http://$ip:$port$path"
    }
}