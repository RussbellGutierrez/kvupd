package com.upd.kvupd.utils

sealed class NetworkRetrofit<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : NetworkRetrofit<T>(data)
    class Error<T>(message: String?, data: T? = null) : NetworkRetrofit<T>(data, message)
}