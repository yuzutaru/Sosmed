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
    val postMedia: List<PostMediaResponseDto>?,
    @SerializedName("profiles")
    val profiles: PostProfileDto?
)

data class PostMediaResponseDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("post_id")
    val postId: String,
    @SerializedName("media_url")
    val url: String,
    @SerializedName("media_type")
    val mediaType: String?
)

data class PostProfileDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("address")
    val address: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?
)
