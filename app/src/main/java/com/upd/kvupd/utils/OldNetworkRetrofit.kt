package com.upd.kvupd.utils

sealed class OldNetworkRetrofit<T>(
    val data: T? = null,
    val message: String? = null
) {
    data class Success<T>(val result: T) : OldNetworkRetrofit<T>(data = result)
    data class Error<T>(val errorMessage: String?, val errorData: T? = null) :
        OldNetworkRetrofit<T>(data = errorData, message = errorMessage)
}