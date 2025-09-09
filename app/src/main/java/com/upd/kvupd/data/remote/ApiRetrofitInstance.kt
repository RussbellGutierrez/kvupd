package com.upd.kvupd.data.remote

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiRetrofitInstance {
    fun createAPI(ip: String, moshi: MoshiConverterFactory): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ip/api/")
            .addConverterFactory(moshi)
            .build()
        return retrofit.create(ApiService::class.java)
    }
}