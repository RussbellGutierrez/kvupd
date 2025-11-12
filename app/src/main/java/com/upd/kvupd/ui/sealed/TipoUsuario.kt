package com.upd.kvupd.ui.sealed

sealed class TipoUsuario(val codigo: String) {
    object Vendedor : TipoUsuario("V")
    object Supervisor : TipoUsuario("S")

    fun nombre(): String = when (this) {
        Vendedor -> "Vendedor"
        Supervisor -> "Supervisor"
    }

    companion object {
        fun nombreDesdeCodigo(codigo: String): String =
            inicialTipo(codigo).nombre()

        fun inicialTipo(codigo: String): TipoUsuario =
            when (codigo) {
                Vendedor.codigo -> Vendedor
                Supervisor.codigo -> Supervisor
                else -> throw IllegalArgumentException("Tipo de usuario desconocido: $codigo")
            }
    }
}