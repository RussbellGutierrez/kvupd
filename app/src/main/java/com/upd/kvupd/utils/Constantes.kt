package com.upd.kvupd.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.upd.kvupd.BuildConfig
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import java.lang.ref.WeakReference
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object BaseDatosRoom {
    const val VERSION_BASEDATOS = 16
    const val DB_NAME = "KventasN"
}

object ConstantsExtras {
    const val NO_FIND_UUID = "/////-/////-/////-/////"
}

object SharedPreferenceKeys {
    const val SHARED_NOMBRE = "KVPREFERENCIA"
    const val KEY_HASH = "hash_id"
    const val KEY_UID = "android_uid"
    const val KEY_ROOM = "version_room"
}

object ConfiguracionFrecuenciaCoordenadas {
    const val TRACKER_INTERVALO_NORMAL = 120_000L
    const val TRACKER_INTERVALO_RAPIDO = 60_000L
    const val TRACKER_LAPSO_EXTENSO = 3_600_000L
    const val FRECUENCIA_METROS = 2.0f
    const val GPS_INTERVALO_NORMAL = 60_000L
    const val GPS_INTERVALO_RAPIDO = 30_000L
    const val IGNORAR_METROS = 0f
}

object DimensionesDialog {
    const val DIALOG_ANCHOTODO = ConstraintLayout.LayoutParams.MATCH_PARENT
    const val DIALOG_ANCHO = ConstraintLayout.LayoutParams.WRAP_CONTENT
    const val DIALOG_ALTO = ConstraintLayout.LayoutParams.WRAP_CONTENT
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

@RequiresApi(Build.VERSION_CODES.O)
object FechaHoraUtil {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    fun ahora(): String = LocalDateTime.now().format(dateTimeFormatter)

    fun dia(): String = LocalDate.now().format(dateFormatter)
}

object WorkerTags {
    const val WORK_CONFIGURACION = "venta_configuracion"
    const val WORK_CLIENTE = "venta_cliente"
    const val WORK_EMPLEADO = "venta_empleado"
    const val WORK_DISTRITO = "venta_distrito"
    const val WORK_NEGOCIO = "venta_negocio"
    const val WORK_RUTA = "venta_ruta"
    const val WORK_ENCUESTA = "venta_encuesta"
}

object FirebaseKeys {
    const val NO_EXISTE = "nothing"
    const val NODO_DIRECCION = "Direccion"
    const val NODO_IP = "Iplocal"//"Ip"
    const val NODO_KVENTAS = "Kventas"
    const val NODO_UUID = "Uuid"
    const val NODO_MODELO = "Modelo"
    const val NODO_FABRICANTE = "Fabricante"
    const val NODO_FECHAHORA = "FechaHora"
    const val NODO_TEMPORAL = "Temporal"
}

object ApisDescargaInicial {
    const val API_CLIENTE = "empleado/movil/cliente"
    const val API_EMPLEADO = "empresa/movil/empleado"
    const val API_RUTA = "empleado/movil/ruta"
    const val API_DISTRITO = "empresa/movil/distrito"
    const val API_NEGOCIO = "empresa/movil/tipo-negocio"
    const val API_CONFIGURACION = "movil/configuracion"
    const val API_ENCUESTA = "empleado/movil/encuesta/lista"
}

object ApisConsultaDatos {
    const val FETCH_LOGIN = "usuario/ingresar"
    const val FETCH_CONSULTA = "empleado/movil/cliente/completo"
    const val FETCH_PEDIMAP = "empleado/movil/marker/empleado"
    const val FETCH_LISTA_BAJAS = "empleado/movil/baja/lista"
    const val FETCH_ESTADOLISTA_BAJAS = "empleado/movil/baja/lista/estado"
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