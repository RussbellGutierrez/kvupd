package com.upd.kvupd.ui.fragment.servidor.modelUI

import com.upd.kvupd.ui.fragment.servidor.enumFile.UploadType
import com.upd.kvupd.ui.sealed.ResultadoApi

data class UploadConfig<T>(
    val type: UploadType,
    val getData: suspend () -> List<T>,
    val send: suspend (T) -> ResultadoApi<Unit>
)