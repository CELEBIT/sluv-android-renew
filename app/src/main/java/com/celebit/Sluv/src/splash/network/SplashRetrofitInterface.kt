package com.celebit.Sluv.src.splash.network

import com.celebit.Sluv.src.splash.models.AutoLoginResponse
import retrofit2.Call
import retrofit2.http.GET

interface SplashRetrofitInterface {
    @GET("/app/auth/auto-login")
    fun postAutoLogin(): Call<AutoLoginResponse>
}