package com.upd.kvupd.ui.fragment.servidor.modelUI

import com.upd.kvupd.ui.fragment.servidor.enumFile.UploadType
import com.upd.kvupd.ui.sealed.ResultadoApi

data class UploadConfig(
    val type: UploadType,
    val getData: suspend () -> List<Any>,
    val send: suspend (Any) -> ResultadoApi<Unit>
)