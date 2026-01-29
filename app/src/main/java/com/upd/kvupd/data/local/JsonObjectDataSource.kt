package com.upd.kvupd.data.local

import android.content.Context
import android.os.Build
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.ui.sealed.TipoUsuario
import com.upd.kvupd.utils.ExtraInfo
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.toReqBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject

class JsonObjectDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun registrarEquipo(identificador: String, empresaNom: String): RequestBody {
        val fabricante = ExtraInfo.obtener(InfoDispositivo.FABRICANTE).uppercase()
        val modelo = ExtraInfo.obtener(InfoDispositivo.MODELO).uppercase()
        val equipo = "$fabricante $modelo"
        val sistema = obtenerSistemaOperativo()
        val empresa = if (empresaNom.lowercase() == "oriunda") 1 else 2
        val uuid = "$identificador-V"
        val json = JSONObject()
        json.put("imei", uuid)
        json.put("modelo", equipo)
        json.put("version", sistema)
        json.put("empresa", empresa)
        return json.toReqBody()
    }

    fun jsonRequestConfiguracion(identificador: String): RequestBody {
        val fabricante = ExtraInfo.obtener(InfoDispositivo.FABRICANTE).uppercase()
        val modelo = ExtraInfo.obtener(InfoDispositivo.MODELO).uppercase()
        val equipo = "$fabricante $modelo"
        val sistema = obtenerSistemaOperativo()
        val uuid = "$identificador-V"
        val json = JSONObject()
        json.put("imei", uuid)
        json.put("modelo", equipo)
        json.put("version", sistema)
        json.put("fecha", FechaHoraUtil.dia())
        return json.toReqBody()
    }

    fun jsonRequestClientes(
        dato: TableConfiguracion,
        vendedor: Int? = null,
        fecha: String? = null
    ): RequestBody {
        val json = JSONObject().apply {
            put("empleado", vendedor ?: dato.codigo)
            put("empresa", dato.empresa)
            put("fecha", fecha ?: dato.fecha)
        }
        return json.toReqBody()
    }

    fun jsonRequestPedimap(dato: TableConfiguracion): RequestBody {
        val json = JSONObject().apply {
            put("empleado", dato.codigo)
            put("empresa", dato.empresa)
            put("esquema", dato.esquema)
        }
        return json.toReqBody()
    }

    fun jsonRequestBasico(dato: TableConfiguracion): RequestBody {
        val json = JSONObject().apply {
            put("empleado", dato.codigo)
            put("empresa", dato.empresa)
        }
        return json.toReqBody()
    }

    fun jsonRequestSimple(dato: TableConfiguracion): RequestBody {
        val json = JSONObject().apply {
            put("empresa", dato.empresa)
        }
        return json.toReqBody()
    }

    fun jsonRequestBajas(dato: TableConfiguracion, baja: TableBaja): RequestBody {
        val tipoUsuario = TipoUsuario.inicialTipo(dato.tipo)
        when (tipoUsuario) {
            TipoUsuario.Vendedor ->
            TipoUsuario.Supervisor -> {}
            TipoUsuario.JefeVentas -> {}
        }
        val json = JSONObject().apply {
            put("empleado", dato.codigo)
            put("empresa", dato.empresa)
        }
        return json.toReqBody()
    }
    /*private fun requestBody(j: TBaja): RequestBody {
        val p = JSONObject()
                p.put("empleado", CONF.codigo)
        p.put("fecha", j.fecha)
        p.put("cliente", j.cliente)
        p.put("motivo", j.motivo)
        p.put("observacion", j.comentario)
        p.put("xcoord", j.longitud)
        p.put("ycoord", j.latitud)
        p.put("precision", j.precision)
        p.put("anulado", j.anulado)
                p.put("empresa", CONF.empresa)
        if (CONF.tipo == "S") {
            p.put("estado",2)
        }
        return p.toReqBody()
    }*/

    private fun obtenerSistemaOperativo(): String {
        val sdkInt = ExtraInfo.obtener(InfoDispositivo.SDK_INT).toInt()
        val versionName = ExtraInfo.obtener(InfoDispositivo.VERSION_APP)

        val versionNameMatch = Build.VERSION_CODES::class.java.fields.firstOrNull { field ->
            try {
                field.getInt(null) == sdkInt
            } catch (e: Exception) {
                false
            }
        }?.name ?: "UNKNOWN"

        return "App: KVU Ver: $versionName API: $sdkInt SO: $versionNameMatch"
    }
}