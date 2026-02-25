package com.upd.kvupd.utils

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.upd.kvupd.BuildConfig
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import java.lang.ref.WeakReference
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object BaseDatosRoom {
    const val VERSION_BASEDATOS = 18
    const val DB_NAME = "KventasN"
}

object ConstantsExtras {
    const val NO_FIND_UUID = "/////-/////-/////-/////"
    const val GPS_FLOW = "GPS_FLOW"
}

object AlarmConstants {
    const val ALARMA_INICIO = "INICIO"
    const val ALARMA_FIN = "FINAL"
}

object SharedPreferenceKeys {
    const val SHARED_NOMBRE = "KVPREFERENCIA"
    const val KEY_HASH = "hash_id"
    const val KEY_UID = "android_uid"
    const val KEY_ROOM = "version_room"
    const val KEY_MODO_GPS = "gps_mode"
    const val KEY_HORA_INICIO = "hora_inicio_gps"
    const val KEY_HORA_FIN = "hora_fin_gps"
    const val KEY_SYNC_INIT = "sync_inicial_ejecutado"
}

object NotificationHelper {
    const val NOTIFICATION_ID = 101
    const val ACTION_OPEN_APP = "com.upd.kvupd.OPEN_APP"
    const val ACTION_RECREATE_NOTIFICATION = "com.upd.kvupd.RECREATE_NOTIFICATION"
    const val ACTION_CHANGE_MODE = "com.upd.kvupd.CHANGE_MODE"
}

object BajaConstantes {
    const val KEY_BAJA = "baja_resultado"
    const val PAIR_BAJA = "baja"
}

object GPSConstants {
    const val INTENT_EXTRA_GPS = "intent_modo"

    const val GPS_CHANNEL = "gps_channel"
    const val GPS_NOTIF_ID = 101

    const val TRACKER_GPS = "rastreo_gps"
    const val GPT_INTERVALO_NORMAL = 120_000L
    const val GPT_INTERVALO_RAPIDO = 60_000L
    const val DISTANCIA_NORMAL = 2.0f
    const val GPT_LAPSO_EXTENSO = 3_600_000L
    const val DISTANCIA_EXTENSO = 50f

    const val MODO_NORMAL = "normal"
    const val MODO_EXTENSO = "extendido"

    const val TRACKER_RAPIDO = "rastreo_rapido"
    const val GPS_INTERVALO_NORMAL = 60_000L
    const val GPS_INTERVALO_RAPIDO = 30_000L
    const val IGNORAR_METROS = 0f

    const val TRACKER_TEMPORAL = "rastreo_temporal"
    const val GT_SIN_INTERVALO = 0L
}

object DimensionesDialog {
    const val DIALOG_ANCHOTODO = ViewGroup.LayoutParams.MATCH_PARENT
    const val DIALOG_ANCHO = ViewGroup.LayoutParams.WRAP_CONTENT
    const val DIALOG_ALTO = ViewGroup.LayoutParams.WRAP_CONTENT
}

object MaterialDialogTexto {
    const val T_SUCCESS = "Correcto"
    const val T_ERROR = "Error"
    const val T_WARNING = "Advertencia"

    const val TEXT_POSITIVO_A = "Ok"
    const val TEXT_POSITIVO_B = "De acuerdo"
    const val TEXT_CANCELAR = "Cancelar"

    const val TAG_DIALOG = "materialDialog"
}

object InstanciaDialog {
    var REFERENCIA_DIALOG: WeakReference<MaterialDialog>? = null

    fun cerrarDialogActual() {
        REFERENCIA_DIALOG?.get()?.dismiss()
        REFERENCIA_DIALOG = null
    }
}

object ExtraInfo {
    fun obtener(opcion: InfoDispositivo): String {
        return when (opcion) {
            InfoDispositivo.FABRICANTE -> Build.MANUFACTURER
            InfoDispositivo.MODELO -> Build.MODEL
            InfoDispositivo.SDK_INT -> Build.VERSION.SDK_INT.toString()
            InfoDispositivo.VERSION_APP -> BuildConfig.VERSION_NAME
        }
    }
}

@SuppressLint("ConstantLocale")
object FechaHoraUtil {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    fun ahora(): String = LocalDateTime.now().format(dateTimeFormatter)

    fun dia(): String = LocalDate.now().format(dateFormatter)

    fun esHoy(fecha: String): Boolean = LocalDate.parse(fecha) == LocalDate.now()

    fun castApi(fecha: String): String {
        return try {
            val inFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            val localDate = LocalDate.parse(fecha, inFmt)
            localDate.format(dateFormatter)
        } catch (e: Exception) {
            fecha
        }
    }
}

object FirebaseKeys {
    const val NO_EXISTE = "nothing"
    const val NODO_DIRECCION = "Direccion"
    const val NODO_IP = "Ip"
    const val NODO_KVENTAS = "Kventas"
    const val NODO_UUID = "Uuid"
    const val NODO_MODELO = "Modelo"
    const val NODO_FABRICANTE = "Fabricante"
    const val NODO_FECHAHORA = "FechaHora"
    const val NODO_PEDIMAP = "Pedimap"
    const val NODO_MENSAJE = "Mensaje"
    const val NODO_TEMPORAL = "Temporal"
}

object ApisDescargaDatos {
    const val API_CLIENTE = "empleado/movil/cliente"
    const val API_EMPLEADO = "empresa/movil/empleado"
    const val API_RUTA = "empleado/movil/ruta"
    const val API_DISTRITO = "empresa/movil/distrito"
    const val API_NEGOCIO = "empresa/movil/tipo-negocio"
    const val API_CONFIGURACION = "movil/configuracion"
    const val API_ENCUESTA = "empleado/movil/encuesta/lista"
    const val API_BAJAS_SUPERVISOR = "empleado/movil/baja/lista"
}

object ApisConsultaDatos {
    const val FETCH_LOGIN = "usuario/ingresar"
    const val FETCH_CONSULTA = "empleado/movil/cliente/completo"
    const val FETCH_PEDIMAP = "empleado/movil/marker/empleado"
    const val FETCH_BAJAS_VENDEDOR = "empleado/movil/baja/lista/estado"
}

object ApisEnviarServidor {
    const val SEND_REGISTRO = "movil/nuevo"
    const val SEND_SEGUIMIENTO = "empleado/movil/seguimiento"
    const val SEND_VISITA = "empleado/movil/visita"
    const val SEND_ALTA = "empleado/movil/alta"
    const val SEND_ALTADETALLE = "empleado/movil/alta/detalle"
    const val SEND_ALTAFOTO = "empleado/movil/alta/documento/foto"
    const val SEND_BAJA = "empleado/movil/baja"
    const val SEND_CONFIRMAR_BAJA = "empleado/movil/baja/confirmar"
    const val SEND_RESPUESTA = "empleado/movil/encuesta/respuesta"
    const val SEND_FOTOS = "empleado/movil/encuesta/foto"
}

object ApisReporteVentas {
    const val REPORT_PREVENTA = "empleado/movil/volumen/general"
    const val REPORT_COBERTURA = "empleado/movil/cobertura/general"
    const val REPORT_COBERTURA_DETALLE = "empleado/movil/pepito"
    const val REPORT_CARTERA = "empleado/movil/cobertura/efectividad"
    const val REPORT_GENERAL = "empleado/movil/preventa/reporte-general"
    const val REPORT_CLIENTE_CAMBIO = "empleado/movil/preventa/cliente-cambio"
    const val REPORT_EMPLEADO_CAMBIO = "empleado/movil/preventa/empleado-cambio"
    const val REPORT_SOLES = "empleado/movil/volumen/linea"
    const val REPORT_SOLES_GENERICO = "empleado/movil/volumen/generico"
    const val REPORT_COBERTURA_PENDIENTE = "empleado/movil/preventa/cobertura-pendiente"
    const val REPORT_EMPLEADO = "empleado/movil/preventa/reporte-empleado"
}