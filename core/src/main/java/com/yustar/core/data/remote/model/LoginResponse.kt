package com.yustar.core.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("expires_at")
    val expiresAt: Long,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("user")
    val user: AuthResponse,
    @SerializedName("weak_password")
    val weakPassword: Any? = null
)
