package com.upd.kvupd.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.upd.kvupd.ui.fragment.type.MapData
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlowCliente(
    @ColumnInfo(name = "cliente") val cliente: String,
    @ColumnInfo(name = "nomcli") val nomcli: String,
    @ColumnInfo(name = "vendedor") val vendedor: Int,
    @ColumnInfo(name = "nomemp") val nomemp: String,
    @ColumnInfo(name = "domicli") val domicilio: String,
    @ColumnInfo(name = "longitud") val longitud: Double,
    @ColumnInfo(name = "latitud") val latitud: Double,
    @ColumnInfo(name = "ruta") val ruta: Int,
    @ColumnInfo(name = "baja") val baja: Int,
    @ColumnInfo(name = "ventas") val ventas: Int,
    @ColumnInfo(name = "ventanio") val compras: Int,
    @ColumnInfo(name = "fecha") val fecha: String,
    @ColumnInfo(name = "negocio") val negocio: String
): Parcelable, MapData {
    @IgnoredOnParcel
    override val mapId: String
        get() = cliente
}

@Parcelize
data class FlowBajaSupervisor(
    @ColumnInfo(name = "empleado") val vendedor: String,
    @ColumnInfo(name = "nombre") val vendnom: String,
    @ColumnInfo(name = "creado") val fecha: String,
    @ColumnInfo(name = "motivo") val motivo: String,
    @ColumnInfo(name = "clicodigo") val cliente: String,
    @ColumnInfo(name = "clinombre") val nombre: String,
    @ColumnInfo(name = "direccion") val direccion: String,
    @ColumnInfo(name = "canal") val canal: String,
    @ColumnInfo(name = "observacion") val observacion: String,
    @ColumnInfo(name = "negocio") val negocio: String,
    @ColumnInfo(name = "pago") val pago: String,
    @ColumnInfo(name = "compra") val compra: String,
    @ColumnInfo(name = "revisado") val revisado: Int,
    @ColumnInfo(name = "procede") val procede: Int?
): Parcelable

@Parcelize
data class BajaAux(
    val cliente: FlowCliente,
    val motivo: Int,
    val comentario: String,
    val fecha: String
): Parcelable

data class BotonesConfig(
    val vendedor: Boolean = false,
    val cartera: Boolean = false,
    val cliente: Boolean = false,
    val reporte: Boolean = false,
    val encuesta: Boolean = false,
    val alta: Boolean = false,
    val baja: Boolean = false,
    val servidor: Boolean = false
)