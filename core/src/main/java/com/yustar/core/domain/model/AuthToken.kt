package com.yustar.core.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val user: User
)
