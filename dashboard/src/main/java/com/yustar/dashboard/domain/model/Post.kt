package com.yustar.dashboard.domain.model

data class Post(
    val id: String,
    val createdAt: String,
    val content: String?,
    val userId: String?,
    val postMedia: List<PostMedia>
)

data class PostMedia(
    val id: String,
    val postId: String,
    val url: String,
    val mediaType: String?
)
