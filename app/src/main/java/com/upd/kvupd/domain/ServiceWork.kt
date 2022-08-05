package com.upd.kvupd.domain

interface ServiceWork {
    fun onSinchronizeData()
    fun onFinishWork(work: String)
}