package com.upd.kvupd.utils

import com.upd.kvupd.utils.OldConstant.BASE_URL
import com.upd.kvupd.utils.OldConstant.IP_AUX
import com.upd.kvupd.utils.OldConstant.IP_P
import com.upd.kvupd.utils.OldConstant.IP_S
import com.upd.kvupd.utils.OldConstant.OPTURL
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OldHostSelectionInterceptor @Inject constructor() :
    Interceptor {
    @Volatile
    private var host = BASE_URL.toHttpUrlOrNull()!!

    init {
        setHostBaseUrl()
    }

    fun setHostBaseUrl() {
        host = when (OPTURL) {
            "base" -> BASE_URL.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid URL Interceptor BASE")
            "ipp" -> IP_P.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid URL Interceptor IPP")
            "ips" -> IP_S.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid URL Interceptor IPS")
            "aux" -> IP_AUX.toHttpUrlOrNull() ?: throw IllegalArgumentException("Invalid URL Interceptor AUX")
            else -> throw IllegalArgumentException("Invalid Opt URL")
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        val newUrl: HttpUrl = request.url.newBuilder()
            .scheme(host.scheme)
            .host(host.toUrl().toURI().host)
            .build()

        request = newUrl.let {
            request.newBuilder()
                .url(it)
                .build()
        }
        return chain.proceed(request)
    }
}