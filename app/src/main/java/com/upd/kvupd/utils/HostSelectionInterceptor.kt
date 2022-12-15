package com.upd.kvupd.utils

import com.upd.kvupd.utils.Constant.BASE_URL
import com.upd.kvupd.utils.Constant.IP_AUX
import com.upd.kvupd.utils.Constant.IP_P
import com.upd.kvupd.utils.Constant.IP_S
import com.upd.kvupd.utils.Constant.OPTURL
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostSelectionInterceptor @Inject constructor() :
    Interceptor {
    @Volatile
    private var host = BASE_URL.toHttpUrlOrNull()

    init {
        setHostBaseUrl()
    }

    fun setHostBaseUrl() {
        when (OPTURL) {
            "base" -> host = BASE_URL.toHttpUrlOrNull()
            "ipp" -> host = IP_P.toHttpUrlOrNull()
            "ips" -> host = IP_S.toHttpUrlOrNull()
            "aux" -> host = IP_AUX.toHttpUrlOrNull()
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (host != null) {
            var newUrl: HttpUrl? = null
            try {
                newUrl = request.url.newBuilder()
                    .scheme(host!!.scheme)
                    .host(host!!.toUrl().toURI().host)
                    .build()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            assert(newUrl != null)
            request = newUrl?.let {
                request.newBuilder()
                    .url(it)
                    .build()
            }!!
        }
        return chain.proceed(request)
    }
}