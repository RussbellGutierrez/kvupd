package com.upd.kvupd.ui.fragment.altas.enumAltaDatos

enum class Documento(val label: String, val code: String) {
    RUC("RUC", "RUC"),
    DNI("DNI", "DNI"),
    CARNET("CARNET", "CE");

    override fun toString() = label

    companion object {
        fun byTipo(tipo: TipoPersona): List<Documento> {
            return when (tipo) {
                TipoPersona.JURIDICA -> listOf(RUC)
                TipoPersona.NATURAL -> entries
            }
        }

        fun from(code: String?): Documento {
            return entries.firstOrNull { it.code == code } ?: RUC
        }
    }
}