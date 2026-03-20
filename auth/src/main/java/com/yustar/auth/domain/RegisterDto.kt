package com.yustar.auth.domain

/**
 * Created by Yustar Pramudana on 07/03/26.
 */

data class RegisterDto(
    val username: String,
    val password: String,
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val phoneNumber: String = ""
)
