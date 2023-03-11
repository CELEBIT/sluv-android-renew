package com.sluv.sluv.src.login.network

import android.util.Log
import com.sluv.sluv.config.ApplicationClass
import com.sluv.sluv.src.login.LoginActivityView
import com.sluv.sluv.src.login.models.LoginRequest
import com.sluv.sluv.src.login.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginService(val view: LoginActivityView) {

    val TAG = "LoginService"

    fun tryPostLogin(loginRequest: LoginRequest) {
        Log.d(TAG, "request : ${loginRequest}")
        val loginRetrofitInterface = ApplicationClass.sRetrofit.create(LoginRetrofitInterface::class.java)
        loginRetrofitInterface.postLogIn(loginRequest).enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "LoginService - onResponse() : 소셜 로그인 API 호출 성공")
                Log.d(TAG, "response : ${response}")
                if (response.body() == null) {
                    view.onPostLoginFailure("response is null")
                } else {
                    view.onPostLoginSuccess(response.body() as LoginResponse)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d(TAG, "LoginService - onResponse() : 카카오 로그인 API 호출 실패")
                view.onPostLoginFailure(t.message ?: "통신오류")
            }
        })
    }
}




