package com.upd.kvupd.data.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ApiRetrofitInstance {

    private const val API_TIMEOUT_SECONDS = 180L

    private val okHttpClient = OkHttpClient.Builder()
        .callTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

    fun createAPI(
        ip: String,
        moshi: MoshiConverterFactory
    ): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip/api/")
            .client(okHttpClient)
            .addConverterFactory(moshi)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}