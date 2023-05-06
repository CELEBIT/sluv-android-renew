package com.celebit.Sluv.src.login.models

import com.google.gson.annotations.SerializedName
import com.celebit.Sluv.config.BaseResponse

data class LoginResponse(
    @SerializedName("result") val result: LoginResult
): BaseResponse()

data class LoginResult(
    @SerializedName("token") val token: String
)
