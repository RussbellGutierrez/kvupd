package com.upd.kvupd.domain

interface OldOnClosingApp {
    fun closingActivity(notRegister: Boolean = false)
    fun closeServiceSetup()
}