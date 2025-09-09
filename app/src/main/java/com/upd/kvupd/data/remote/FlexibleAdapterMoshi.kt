package com.upd.kvupd.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

class FlexibleAdapterMoshi {

    // --- DESERIALIZACIÓN: de JSON -> Kotlin (Any?) ---
    @FromJson
    fun fromJson(reader: JsonReader): Any? {
        return when (reader.peek()) {
            // Caso: JSON Object → Map<String, Any?>
            JsonReader.Token.BEGIN_OBJECT -> {
                (reader.readJsonValue() as? Map<*, *>)      // Lo leemos como Map genérico
                    ?.mapKeys { it.key.toString() }         // Convertimos las claves a String
            }

            // Caso: JSON Array → List<Map<String, Any?>>
            JsonReader.Token.BEGIN_ARRAY -> {
                (reader.readJsonValue() as? List<*>)        // Lo leemos como lista genérica
                    ?.mapNotNull { item ->
                        (item as? Map<*, *>)?.mapKeys { it.key.toString() }
                    }
            }

            // Caso: null → avanzamos el cursor y devolvemos null
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }

            // Caso por defecto: string, number, boolean → lo leemos tal cual
            else -> reader.readJsonValue()
        }
    }

    // --- SERIALIZACIÓN: de Kotlin (Any?) -> JSON ---
    @ToJson
    fun toJson(writer: JsonWriter, value: Any?) {
        when (value) {
            // Caso null → se escribe como JSON null
            null -> writer.nullValue()

            // Caso Map → se abre un objeto JSON y se escribe cada par clave/valor
            is Map<*, *> -> {
                writer.beginObject()
                for ((k, v) in value) {
                    writer.name(k.toString())
                    toJson(writer, v) // recursión para valores anidados
                }
                writer.endObject()
            }

            // Caso List → se abre un array JSON y se escribe cada elemento
            is List<*> -> {
                writer.beginArray()
                for (item in value) {
                    toJson(writer, item) // recursión para elementos anidados
                }
                writer.endArray()
            }

            // Casos simples → se escriben directamente
            is Number -> writer.value(value)
            is Boolean -> writer.value(value)
            is String -> writer.value(value)

            // Fallback: cualquier otro tipo → se convierte a string
            else -> writer.value(value.toString())
        }
    }
}