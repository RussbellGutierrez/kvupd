package com.upd.kvupd.ui.fragment.servidor.enumFile

import androidx.annotation.ColorRes
import com.upd.kvupd.R

enum class ApiServerStatus(@ColorRes val colorRes: Int) {
    IDLE(R.color.lightgray),
    LOADING(R.color.gold),
    SUCCESS(R.color.lightgreen),
    ERROR(R.color.lightcrimson)
}