package com.upd.kvupd.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.data.model.core.TableBajaProcesada
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.core.TableFoto
import com.upd.kvupd.data.model.core.TableRespuesta
import com.upd.kvupd.data.model.core.TableSeguimiento
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.utils.DeviceConstant.APP_ORIGIN
import com.upd.kvupd.utils.ExtraInfo
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.toReqBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class JsonObjectDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun registrarEquipo(identificador: String, empresaNom: String): RequestBody {
        val empresa = if (empresaNom.lowercase() == "oriunda") 1 else 2

        return baseDevice(identificador).apply {
            put("empresa", empresa)
        }.toReqBody()
    }

    fun jsonRequestConfiguracion(identificador: String): RequestBody {
        return baseDevice(identificador).apply {
            put("fecha", FechaHoraUtil.dia())
        }.toReqBody()
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
        return baseConfig(dato).apply {
            put("esquema", dato.esquema)
        }.toReqBody()
    }

    fun jsonRequestBasico(dato: TableConfiguracion): RequestBody {
        return baseConfig(dato).toReqBody()
    }

    fun jsonRequestSimple(dato: TableConfiguracion): RequestBody {
        val json = JSONObject().apply {
            put("empresa", dato.empresa)
        }
        return json.toReqBody()
    }

    fun jsonRequestSeguimiento(
        dato: TableConfiguracion,
        gps: TableSeguimiento,
        uuid: String
    ): RequestBody {
        val imei = "$uuid$APP_ORIGIN"
        val json = JSONObject().apply {
            put("sucursal", dato.sucursal)
            put("esquema", dato.esquema)
            put("empresa", dato.empresa)
            put("fecha", gps.fecha)
            put("empleado", gps.usuario)
            put("longitud", gps.longitud)
            put("latitud", gps.latitud)
            put("precision", gps.precision)
            put("imei", imei)
            put("bateria", gps.bateria)
        }
        return json.toReqBody()
    }

    fun jsonRequestBajas(dato: TableConfiguracion, baja: TableBaja): RequestBody {
        val tipoUsuario = TipoUsuario.fromCodigo(dato.tipo)
        val estado = when (tipoUsuario) {
            TipoUsuario.VENDEDOR -> 1
            TipoUsuario.SUPERVISOR,
            TipoUsuario.JEFE_VENTAS -> 2
        }
        return baseConfig(dato).apply {
            put("estado", estado)
            put("fecha", baja.fecha)
            put("cliente", baja.cliente)
            put("motivo", baja.motivo)
            put("observacion", baja.comentario)
            put("xcoord", baja.longitud)
            put("ycoord", baja.latitud)
            put("precision", baja.precision)
            put("anulado", baja.anulado)
        }.toReqBody()
    }

    fun jsonRequestBajasProcesadas(
        dato: TableConfiguracion,
        baja: TableBajaProcesada
    ): RequestBody {
        val json = JSONObject().apply {
            put("empleado", baja.empleado)
            put("empresa", dato.empresa)
            put("fecha", baja.fecha)
            put("cliente", baja.cliente)
            put("cfecha", baja.fechaconfirmacion)
            put("observacion", baja.observacion)
            put("precision", baja.precision)
            put("xcoord", baja.longitud)
            put("ycoord", baja.latitud)
            put("confirmar", baja.procede)
        }
        return json.toReqBody()
    }

    fun jsonRequestRespuesta(
        dato: TableConfiguracion,
        respuesta: TableRespuesta
    ): RequestBody {
        return baseConfig(dato).apply {
            put("cliente", respuesta.cliente)
            put("encuesta", respuesta.encuesta)
            put("pregunta", respuesta.pregunta)
            put("respuesta", respuesta.respuesta)
            put("xcoord", respuesta.longitud)
            put("ycoord", respuesta.latitud)
            put("fecha", respuesta.fecha)
        }.toReqBody()
    }

    fun jsonRequestFoto(
        dato: TableConfiguracion,
        foto: TableFoto
    ): RequestBody {
        val decode = convertirFotoBase64(foto.rutafoto)

        return baseConfig(dato).apply {
            put("cliente", foto.cliente)
            put("encuesta", foto.encuesta)
            put("sucursal", dato.sucursal)
            put("foto", decode)
        }.toReqBody()
    }

    fun jsonRequestAlta(
        dato: TableConfiguracion,
        alta: TableAlta
    ): RequestBody {
        val json = JSONObject().apply {
            put("empleado", alta.empleado)
            put("empresa", dato.empresa)
            put("fecha", alta.fecha)
            put("id", alta.idaux)
            put("longitud", alta.longitud)
            put("latitud", alta.latitud)
            put("precision", alta.precision)
            put("sucursal", dato.sucursal)
            put("esquema", dato.esquema)
        }
        return json.toReqBody()
    }

    fun jsonRequestAltaDatos(
        dato: TableConfiguracion,
        altadato: TableAltaDatos
    ): RequestBody {
        val json = JSONObject().apply {
            put("empleado", altadato.empleado)
            put("id", altadato.idaux)
            put("appaterno", altadato.appaterno)
            put("apmaterno", altadato.apmaterno)
            put("nombre", altadato.nombre)
            put("razon", altadato.razon)
            put("tipo", altadato.tipo)
            put("dnice", altadato.dnice)
            put("ruc", altadato.ruc)
            put("tdoc", altadato.tipodocu)
            put("giro", altadato.giro)
            put("movil1", altadato.movil1)
            put("movil2", altadato.movil2)
            put("email", altadato.correo)
            put("urbanizacion", "${altadato.zona} ${altadato.zonanombre}")
            put("altura", altadato.numero)
            put("distrito", altadato.distrito)
            put("ruta", altadato.ruta)
            put("secuencia", altadato.secuencia)
            put("sucursal", dato.sucursal)
            put("esquema", dato.esquema)
            put("empresa", dato.empresa)
            put("observacion", altadato.observacion)
            put("calle", altadato.buildCalle())
        }
        return json.toReqBody()
    }

    fun jsonRequestReport(
        dato: TableConfiguracion,
        linea: Int? = null,
        marca: Int? = null
    ): RequestBody {
        return baseConfig(dato).apply {
            linea?.let { put("linea", it) }
            marca?.let { put("marca", it) }
        }.toReqBody()
    }

    private fun baseConfig(dato: TableConfiguracion): JSONObject {
        return JSONObject().apply {
            put("empleado", dato.codigo)
            put("empresa", dato.empresa)
        }
    }

    private fun baseDevice(identificador: String): JSONObject {
        val fabricante = ExtraInfo.obtener(InfoDispositivo.FABRICANTE).uppercase()
        val modelo = ExtraInfo.obtener(InfoDispositivo.MODELO).uppercase()

        return JSONObject().apply {
            put("imei", "$identificador$APP_ORIGIN")
            put("modelo", "$fabricante $modelo")
            put("version", obtenerSistemaOperativo())
        }
    }

    private fun TableAltaDatos.buildCalle(): String =
        if (manzana.isEmpty()) {
            "$via $direccion $ubicacion"
        } else {
            "$via $direccion MZ $manzana $ubicacion"
        }

    private fun convertirFotoBase64(ruta: String): String {
        val bm = BitmapFactory.decodeFile(ruta) ?: return ""

        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)

        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

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