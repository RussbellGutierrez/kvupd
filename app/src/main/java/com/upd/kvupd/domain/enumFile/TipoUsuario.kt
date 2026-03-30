package com.upd.kvupd.domain.enumFile

enum class TipoUsuario(
    val codigo: String,
    val nombre: String
) {
    VENDEDOR("V", "Vendedor"),
    SUPERVISOR("S", "Supervisor"),
    JEFE_VENTAS("G", "Jefe Ventas");

    companion object {
        fun fromCodigo(codigo: String): TipoUsuario =
            entries.firstOrNull { it.codigo == codigo }
                ?: throw IllegalArgumentException(
                    "Tipo de usuario desconocido: $codigo"
                )
    }
}