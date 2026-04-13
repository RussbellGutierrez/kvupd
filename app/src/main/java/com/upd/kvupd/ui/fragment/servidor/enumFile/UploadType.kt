package com.upd.kvupd.ui.fragment.servidor.enumFile

enum class UploadType(
    val titulo: String
) {
    GPS("GPS"),
    ALTAS("Altas de clientes"),
    ALTA_DATOS("Datos de altas"),
    BAJAS("Bajas de clientes"),
    BAJA_REVISADA("Revisiones de bajas"),
    ENCUESTAS("Encuestas resueltas"),
    FOTOS("Fotos de encuestas")
}