package com.upd.kvupd.ui.fragment.servidor.enumFile

import android.graphics.Color
import com.upd.kvupd.R

enum class ApiStatus(val color: Int) {
    IDLE(R.color.lightgray),
    LOADING(R.color.gold),
    SUCCESS(R.color.green),
    ERROR(R.color.lightcrimson)
}