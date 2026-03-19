package com.yustar.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val address: String,
    val phoneNumber: String
)
