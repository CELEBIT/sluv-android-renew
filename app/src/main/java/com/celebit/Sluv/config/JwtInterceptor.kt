package com.celebit.Sluv.config

import android.util.Log
import com.celebit.Sluv.config.ApplicationClass.Companion.ACCESS_TOKEN
import com.celebit.Sluv.config.ApplicationClass.Companion.prefs
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class JwtInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        val jwtToken: String? = prefs.getString(ACCESS_TOKEN, null)
        if (jwtToken != null && jwtToken != "null") {
            builder.addHeader("Authorization", "Bearer $jwtToken")
            Log.d("JwtInterceptor", "if 문 실행")
        }
        return chain.proceed(builder.build())
    }
}