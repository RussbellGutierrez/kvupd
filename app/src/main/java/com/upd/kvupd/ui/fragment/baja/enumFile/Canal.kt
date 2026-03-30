package com.upd.kvupd.ui.fragment.baja.enumFile

import com.upd.kvupd.R

enum class Canal(
    val codigo: String,
    val iconRes: Int
) {
    BRONCE("BRONCE", R.drawable.bronce),
    PLATA("PLATA", R.drawable.plata),
    ORO("ORO", R.drawable.oro),
    DIAMANTE("DIAMANTE", R.drawable.diamante),
    MINORISTA("MINORISTA", R.drawable.minorista),
    MAYORISTA("MAYORISTA", R.drawable.mayorista),
    HORIZONTAL("HORIZONTAL", R.drawable.horizontal),
    MERCADOS("MERCADOS", R.drawable.mercado),
    BOTICAS("BOTICAS", R.drawable.pildora),
    DESCONOCIDO("DESCONOCIDO", R.drawable.restringido);

    companion object {
        fun fromCodigo(codigo: String?): Canal =
            entries.firstOrNull { it.codigo == codigo }
                ?: DESCONOCIDO
    }
}