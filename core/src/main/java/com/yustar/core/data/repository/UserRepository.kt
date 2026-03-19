package com.yustar.core.data.repository

import com.yustar.core.data.local.User

interface UserRepository {
    suspend fun register(
        username: String,
        password: String,
        firstName: String = "",
        lastName: String = "",
        address: String = "",
        phoneNumber: String = ""
    )
    suspend fun login(username: String, password: String): Boolean
    suspend fun getUser(username: String): User?
    suspend fun updateUserProfile(
        username: String,
        firstName: String? = null,
        lastName: String? = null,
        address: String? = null,
        phoneNumber: String? = null
    )
}