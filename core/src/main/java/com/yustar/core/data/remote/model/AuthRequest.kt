package com.yustar.core.data.remote.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("data")
    val data: RegisterData
)

data class RegisterData(
    @SerializedName("username")
    val username: String
)
