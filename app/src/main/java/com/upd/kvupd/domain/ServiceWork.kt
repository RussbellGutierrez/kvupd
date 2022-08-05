package com.upd.kvupd.domain

import com.upd.kvupd.data.model.TIncidencia

interface ServiceWork {

    fun onSinchronizeData()
    fun onFinishWork(work: String)
    fun savingSystemReport(item: TIncidencia)
}