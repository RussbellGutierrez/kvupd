package com.upd.kvupd.utils

import android.location.Location
import androidx.constraintlayout.widget.ConstraintLayout
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.data.model.TConfiguracion

object Constant {

    const val M_PEDIDO = "Hizo pedido"
    const val M_CERRADO = "Puesto cerrado"
    const val M_PRODUCTO = "Tiene producto"
    const val M_DINERO = "Sin dinero"
    const val M_ENCARGADO = "Sin encargado"
    const val M_OCUPADO = "Cliente ocupado"
    const val M_NOEXISTE = "Cliente no existe"
    const val M_ALTA = "Alta cliente"

    var MSG_CONFIG = "Message"
    var MSG_USER = "Message"
    var MSG_DISTRITO = "Message"
    var MSG_NEGOCIO = "Message"
    var MSG_RUTA = "Message"
    var MSG_ENCUESTA = "Message"

    var IS_SUNDAY = false
    var IS_CONFIG_FAILED = false
    var TAP_GPS_ONCE = false

    var IMEI = ""
    var PROCEDE = ""
    var VISICOOLER_ID = 0
    lateinit var POS_LOC: Location
    lateinit var ALTADATOS: String
    lateinit var GPS_LOC: Location
    lateinit var CONF: TConfiguracion
    lateinit var IWAM: DataCliente
    lateinit var IWP: Pedimap

    fun isPOSLOCinitialized(): Boolean = ::POS_LOC.isInitialized
    fun isCONFinitialized(): Boolean = ::CONF.isInitialized

    const val PERIODIC_WORK = "WorkVentas"
    const val W_SETUP = "VSetup"
    const val W_FINISH = "VFinish"
    const val W_CONFIG = "VConfiguracion"
    const val W_USER = "VUser"
    const val W_DISTRITO = "VDistrito"
    const val W_NEGOCIO = "VNegocio"
    const val W_RUTA = "VRuta"
    const val W_ENCUESTA = "VEncuesta"

    const val WP_SEGUIMIENTO = "VPSeguimiento"
    const val WP_VISITA = "VPVisita"
    const val WP_ALTA = "VPAlta"
    const val WP_ALTADATO = "VPAltadato"
    const val WP_BAJA = "VPBaja"
    const val WP_BAJAESTADO = "VPBajaestado"
    const val WP_RESPUESTA = "VPRespuesta"
    const val WP_FOTO = "VPFoto"

    const val SETUP_CHANNEL = "1"
    const val SETUP_NOTIF = 1
    const val CONFIG_CHANNEL = "2"
    const val CONFIG_NOTIF = 2
    const val USER_CHANNEL = "3"
    const val USER_NOTIF = 3
    const val DISTRITO_CHANNEL = "4"
    const val DISTRITO_NOTIF = 4
    const val NEGOCIO_CHANNEL = "5"
    const val NEGOCIO_NOTIF = 5
    const val RUTA_CHANNEL = "6"
    const val RUTA_NOTIF = 6
    const val ENCUESTA_CHANNEL = "7"
    const val ENCUESTA_NOTIF = 7

    const val GPS_NORMAL_INTERVAL = 300000L
    const val GPS_FAST_INTERVAL = 120000L
    const val POSITION_N_INTERVAL = 60000L
    const val POSITION_F_INTERVAL = 30000L
    const val DL_WIDTH = ConstraintLayout.LayoutParams.MATCH_PARENT
    const val D_WIDTH = ConstraintLayout.LayoutParams.WRAP_CONTENT
    const val D_HEIGHT = ConstraintLayout.LayoutParams.WRAP_CONTENT
    const val BASE_URL = "http://200.110.40.58/api/"
    const val SOCKET_ORIUNDA = "http://200.110.40.58:8080/oriunda/update"
    const val SOCKET_TERRANORTE = "http://200.110.40.58:80/terranorte/update"
    const val DB_NAME = "KventasN"
    const val REQ_CODE = 101
    const val REQ_BACK_CODE = 102

    // Regex para el formato de IPv4
    const val IP_FILTER = "^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}" +
            "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$"

    // Api Request
    const val API_LOGIN = "usuario/ingresar"
    const val API_CLIENTE = "empleado/movil/cliente"
    const val API_EMPLEADO = "empresa/movil/empleado"
    const val API_RUTA = "empleado/movil/ruta"
    const val API_DISTRITO = "empresa/movil/distrito"
    const val API_NEGOCIO = "empresa/movil/tipo-negocio"
    const val API_REGISTRO = "movil/nuevo"
    const val API_CONFIGURACION = "movil/configuracion"
    const val API_ENCUESTA = "empleado/movil/encuesta/lista"

    const val API_PREVENTA = "empleado/movil/volumen/general"
    const val API_COBERTURA = "empleado/movil/cobertura/general"
    const val API_CARTERA = "empleado/movil/cobertura/efectividad"
    const val API_REPOGEN = "empleado/movil/preventa/reporte-general"
    const val API_VISIC = "empleado/movil/cliente/equipo-frio"
    const val API_VISICSUPER = "empleado/movil/empleado/equipo-frio"
    const val API_CLICAMBIO = "empleado/movil/preventa/cliente-cambio"
    const val API_EMPCAMBIO = "empleado/movil/preventa/empleado-cambio"
    const val API_UMES = "empleado/movil/ume/linea"
    const val API_SOLES = "empleado/movil/volumen/linea"

    const val API_UMESGEN = "empleado/movil/ume/generico"
    const val API_SOLESGEN = "empleado/movil/volumen/generico"
    const val API_UMESDET = "empleado/movil/ume/empleado"
    const val API_COBPEN = "empleado/movil/preventa/cobertura-pendiente"
    const val API_REPOEMP = "empleado/movil/preventa/reporte-empleado"

    const val API_EMPMARCADOR = "empleado/movil/marker/empleado"
    const val API_BAJALIS = "empleado/movil/baja/lista"
    const val API_BAJAESTLIS = "empleado/movil/baja/lista/estado"

    /*  Send server */
    const val API_SEGUIMIENTO = "empleado/movil/seguimiento"
    const val API_VISITA = "empleado/movil/visita"
    const val API_ALTA = "empleado/movil/alta"
    const val API_ALTADETALLE = "empleado/movil/alta/detalle"
    const val API_BAJA = "empleado/movil/baja"
    const val API_BAJACONFIR = "empleado/movil/baja/confirmar"
    const val API_RESPUESTA = "empleado/movil/encuesta/respuesta"
    const val API_FOTO = "empleado/movil/encuesta/foto"

    //const val API_ALTALIS = "empleado/movil/alta/lista"
    //const val API_ALTAMOD = "empleado/movil/alta/modificar"
    //const val VOUCHER = "empleado/movil/recibo/foto"
    //const val DATOVOUCHER = "empleado/movil/recibo/nuevo"
    //const val COMPRAS = "empleado/movil/reporte/cliente/venta"

}