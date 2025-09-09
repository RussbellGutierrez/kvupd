package com.upd.kvupd.data.remote

import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class ApiBuilder @Inject constructor(
    private val moshiConverterFactory: MoshiConverterFactory,
    private val firebaseHelper: FirebaseHelper
) {
    suspend fun createAPI(): ApiService =
        ApiRetrofitInstance.createAPI(firebaseHelper.obtenerIpFirebase(), moshiConverterFactory)
}