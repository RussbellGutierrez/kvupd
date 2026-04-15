package com.upd.kvupd.ui.fragment.servidor.modelUI

import com.upd.kvupd.ui.fragment.servidor.enumFile.ApiServerStatus
import com.upd.kvupd.ui.fragment.servidor.enumFile.UploadType

data class UploadItem(
    val type: UploadType,
    val total: Int,        // total en DB
    val pending: Int,      // synced = false
    val processed: Int,    // enviados en este ciclo
    val status: ApiServerStatus
)