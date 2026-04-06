package com.upd.kvupd.ui.fragment.reportes.modelUI

import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.ui.sealed.ResultadoApi
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

data class SolesRequestConfig(
    val apiCall: suspend (RequestBody) -> Flow<ResultadoApi<JsonVolumen>>,
    val linea: Int? = null,
    val marca: Int? = null
)