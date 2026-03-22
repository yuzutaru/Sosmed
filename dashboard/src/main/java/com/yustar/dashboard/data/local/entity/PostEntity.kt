package com.yustar.dashboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val createdAt: String,
    val content: String?,
    val userId: String?
)

@Entity(tableName = "post_media")
data class PostMediaEntity(
    @PrimaryKey
    val id: String,
    val postId: String,
    val url: String,
    val mediaType: String?
)
