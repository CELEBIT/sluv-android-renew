package com.celebit.Sluv.src.login.network

import com.celebit.Sluv.src.login.models.LoginRequest
import com.celebit.Sluv.src.login.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {

    @POST("/app/auth/social-login")
    fun postLogIn(@Body params: LoginRequest): Call<LoginResponse>
}