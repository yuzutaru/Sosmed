package com.yustar.dashboard.data.remote.model

import com.google.gson.annotations.SerializedName

data class PostResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("content")
    val content: String?,
    @SerializedName("user_id")
    val userId: String?,
    @SerializedName("post_media")
    val postMedia: List<PostMediaResponseDto>?
)

data class PostMediaResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("post_id")
    val postId: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("media_type")
    val mediaType: String?
)
