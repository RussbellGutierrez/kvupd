package com.upd.kvupd.utils

import retrofit2.Response

abstract class OldBaseApiResponse {

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): OldNetworkRetrofit<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return OldNetworkRetrofit.Success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): OldNetworkRetrofit<T> =
        OldNetworkRetrofit.Error("Error Api: $errorMessage")

}