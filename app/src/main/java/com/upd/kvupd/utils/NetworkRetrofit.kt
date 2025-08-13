package com.upd.kvupd.utils

sealed class NetworkRetrofit<T>(
    val data: T? = null,
    val message: String? = null
) {
    data class Success<T>(val result: T) : NetworkRetrofit<T>(data = result)
    data class Error<T>(val errorMessage: String?, val errorData: T? = null) :
        NetworkRetrofit<T>(data = errorData, message = errorMessage)
}