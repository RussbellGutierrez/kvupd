package com.upd.kvupd.domain

interface OnClosingApp {
    fun closingActivity(notRegister: Boolean = false)
}