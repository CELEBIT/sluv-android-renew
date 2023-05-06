package com.celebit.Sluv.src.splash.models

import com.google.gson.annotations.SerializedName

data class AutoLoginResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)
