package com.upd.kvupd.ui.fragment.altas.enumAltaDatos

enum class TipoPersona(val code: String, val descripcion: String) {
    JURIDICA("PJ", "Persona Jurídica"),
    NATURAL("PN", "Persona Natural");

    override fun toString() = descripcion

    companion object {
        fun from(code: String?): TipoPersona {
            return entries.firstOrNull { it.code == code } ?: NATURAL
        }
    }
}