package com.upd.kvupd.utils

import android.util.Log
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.ui.sealed.ResultadoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

fun JSONObject.toReqBody(): RequestBody =
    this.toString().toRequestBody("application/json".toMediaTypeOrNull())

fun Throwable.respuestaUsuario(): String = when (this) {
    is java.net.UnknownHostException -> "No hay conexión a Internet"
    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
    is retrofit2.HttpException -> "Error del servidor: ${this.code()}"
    else -> this.message ?: "Error desconocido"
}

inline fun <reified T, reified H> remoteFlowCall(
    crossinline setupHolder: suspend () -> H,
    crossinline block: suspend H.() -> Response<T>
): Flow<ResultadoApi<T>> = flow {
    emit(ResultadoApi.Loading)

    try {
        val holder = setupHolder()
        val response = block(holder) // ejecuta en el mismo contexto del flow
        if (response.isSuccessful) {
            emit(ResultadoApi.Exito(response.body()))
        } else {
            emit(ResultadoApi.ErrorHttp(response.code(), response.errorBody()?.string()))
        }
    } catch (e: Exception) {
        Log.e("RemoteFlowCall", "Error atrapado en Flow", e)
        emit(ResultadoApi.Fallo(e))
    }
}.flowOn(Dispatchers.IO)


/** ------------------ EXTRACCIONES SEGURAS ------------------ **/
// Obtener un String seguro
fun JsonResponseAny.getJsonString(key: String): String = this[key] as? String ?: ""

// Obtener un Int seguro
fun JsonResponseAny.getInt(key: String): Int = when (val value = this[key]) {
    is Number -> value.toInt()
    is String -> value.toIntOrNull() ?: 0
    else -> 0
}

// Obtener un Double seguro
fun JsonResponseAny.getJsonDouble(key: String): Double = when (val value = this[key]) {
    is Number -> value.toDouble()
    is String -> value.toDoubleOrNull() ?: 0.0
    else -> 0.0
}

// Obtener un Boolean seguro
fun JsonResponseAny.getJsonBoolean(key: String): Boolean = when (val value = this[key]) {
    is Boolean -> value
    is String -> value.lowercase() == "true"
    is Number -> value.toInt() != 0
    else -> false
}

// Obtener un Map<String, Any?> seguro sin unchecked cast
fun JsonResponseAny.getJsonMap(key: String): Map<String, Any?> {
    val value = this[key]
    return if (value is Map<*, *>) {
        value.filterKeys { it is String }.map { it.key as String to it.value }.toMap()
    } else emptyMap()
}

// Obtener una lista de objetos Map<String, Any?> segura sin unchecked cast
fun JsonResponseAny.getJsonList(key: String): List<Map<String, Any?>> {
    val value = this[key]
    if (value !is List<*>) return emptyList()

    return value.mapNotNull { item ->
        if (item is Map<*, *>) {
            item.filterKeys { it is String }.map { it.key as String to it.value }.toMap()
        } else null
    }
}

// Obtener lista de String segura
fun JsonResponseAny.getJsonStringList(key: String): List<String> {
    val value = this[key]
    if (value !is List<*>) return emptyList()

    return value.mapNotNull { it as? String }
}

// Obtener el primer String de una lista segura
fun JsonResponseAny.getJsonFirstString(key: String): String {
    val value = this[key]
    return if (value is List<*>) {
        value.firstOrNull() as? String ?: ""
    } else ""
}

/** ------------------ EXTENSIONES CON LAMBDA ------------------ **/

// Ejecuta un bloque si el string existe y no es nulo/vacío
fun JsonResponseAny.siExisteStringEjecutar(key: String, block: (String) -> Unit) {
    val value = this[key] as? String
    if (!value.isNullOrEmpty()) block(value)
} // Para String plano

// Itera una lista de Map<String, Any?> y ejecuta un bloque por cada elemento
fun JsonResponseAny.iterarLista(key: String, block: (Map<String, Any?>) -> Unit) {
    getJsonList(key).forEach { block(it) }
} // Para una lista de objetos [{...}, {...}]

// Itera un Map<String, Any?> interno y ejecuta un bloque
fun JsonResponseAny.iterarMap(key: String, block: (String, Any?) -> Unit) {
    getJsonMap(key).forEach { (k, v) -> block(k, v) }
} // Para un objeto/Map {...} dentro del JSON

// Obtener el mensaje de un List Json
fun JsonResponseAny.obtenerMensajeListaJson(padre: String, hijo: String): String =
    getJsonList(padre).firstOrNull()?.get(hijo) as? String ?: ""