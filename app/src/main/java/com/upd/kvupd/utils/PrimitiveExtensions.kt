package com.upd.kvupd.utils

import android.util.Patterns
import com.upd.kvupd.ui.fragment.altas.enumAltaDatos.TipoPersona
import java.time.LocalTime
import java.util.Locale

fun String?.orDefault(default: String): String {
    return if (this == null || this == "null") default else this
}

fun String.toUpper(): String = this.uppercase(Locale.getDefault())

fun String.toLocalTime(): LocalTime = LocalTime.parse(this)

fun Double.to2Decimals(): Double = kotlin.math.round(this * 100) / 100

fun String.isValidEmail(): Boolean {
    return isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return length == 9 && startsWith("9") && all { it.isDigit() }
}

fun String.isValidDocumento(tipo: TipoPersona): Boolean {

    val len = length

    val isDni = len == 8
    val isCarnet = len == 9
    val isRuc = len == 11

    return when (tipo) {

        TipoPersona.JURIDICA -> {
            when {
                isRuc -> startsWith("20")
                isDni || isCarnet -> true
                else -> false
            }
        }

        TipoPersona.NATURAL -> {
            when {
                isDni || isCarnet -> true
                isRuc -> startsWith("10") || startsWith("15")
                else -> false
            }
        }
    }
}

fun String.isValidPositiveNumber(): Boolean {
    return toIntOrNull()?.let { it > 0 } == true
}

fun Double.to0Dec(): String =
    String.format(Locale.US, "%.0f", this)

fun Double.to2Dec(): String =
    String.format(Locale.US, "%.2f", this)