package com.upd.kvupd.ui.fragment.servidor.modelUI

import com.upd.kvupd.ui.fragment.servidor.enumFile.ApiServerStatus

data class ServerStatusResult(
    val status: ApiServerStatus,
    val message: String?
)