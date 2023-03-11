package com.sluv.sluv.src.login.network

import com.sluv.sluv.src.login.models.LoginRequest
import com.sluv.sluv.src.login.models.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginRetrofitInterface {

    @POST("/auth/social-login")
    fun postLogIn(@Body params: LoginRequest): Call<LoginResponse>
}