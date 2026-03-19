package com.yustar.core.data.remote.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("user")
    val user: RefreshTokenUser
)

data class RefreshTokenUser(
    @SerializedName("id")
    val id: String,
    @SerializedName("email")
    val email: String
)
