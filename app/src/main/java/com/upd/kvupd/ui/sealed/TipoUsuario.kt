package com.upd.kvupd.ui.sealed

sealed class TipoUsuario(val codigo: String) {
    object Vendedor : TipoUsuario("V")
    object Supervisor : TipoUsuario("S")
    object JefeVentas : TipoUsuario("G")

    fun nombre(): String = when (this) {
        Vendedor -> "Vendedor"
        Supervisor -> "Supervisor"
        JefeVentas -> "Jefe"
    }

    companion object {
        fun nombreDesdeCodigo(codigo: String): String =
            inicialTipo(codigo).nombre()

        fun inicialTipo(codigo: String): TipoUsuario =
            when (codigo) {
                Vendedor.codigo -> Vendedor
                Supervisor.codigo -> Supervisor
                JefeVentas.codigo -> JefeVentas
                else -> throw IllegalArgumentException("Tipo de usuario desconocido: $codigo")
            }
    }
}