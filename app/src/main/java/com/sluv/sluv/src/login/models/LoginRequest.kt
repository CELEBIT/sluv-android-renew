package com.sluv.sluv.src.login.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("snsType") val snsType: String
)
