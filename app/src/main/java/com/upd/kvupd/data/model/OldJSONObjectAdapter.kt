package com.upd.kvupd.data.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import org.json.JSONObject

// Adaptador personalizado para que Moshi pueda convertir entre JSON y JSONObject
class OldJSONObjectAdapter {

    // Indica a Moshi cómo deserializar (de JSON → JSONObject)
    @FromJson
    fun fromJson(reader: JsonReader): JSONObject? {
        return try {
            val value = reader.readJsonValue() // Lee el contenido del JSON como un objeto dinámico
            if (value is Map<*, *>) {          // Verifica si el resultado es un Map (estructura esperada para JSONObject)
                JSONObject(value)              // Crea un JSONObject usando ese mapa
            } else {
                null                           // Si no es un mapa, no se puede convertir
            }
        } catch (e: Exception) {
            null                               // Si algo falla al parsear, devuelve null
        }
    }

    // Indica a Moshi cómo serializar (de JSONObject → JSON)
    @ToJson
    fun toJson(writer: JsonWriter, value: JSONObject?) {
        if (value == null) {
            writer.nullValue() // Si el JSONObject es null, escribe null en el JSON
            return
        }

        // Comienza un nuevo objeto JSON
        writer.beginObject()
        for (key in value.keys()) { // Itera por cada clave en el JSONObject
            writer.name(key)        // Escribe el nombre de la propiedad en el JSON

            val obj = value.get(key) // Obtiene el valor correspondiente a esa clave
            when (obj) {
                is Number -> writer.value(obj)   // Si es número, lo escribe como número
                is Boolean -> writer.value(obj)  // Si es booleano, lo escribe como booleano
                is String -> writer.value(obj)   // Si es string, lo escribe como string
                is JSONObject -> toJson(writer, obj) // Si es un JSONObject anidado, lo procesa recursivamente
                else -> writer.nullValue()       // Si es nulo o un tipo no soportado, lo escribe como null
            }
        }
        writer.endObject() // Finaliza el objeto JSON
    }
}