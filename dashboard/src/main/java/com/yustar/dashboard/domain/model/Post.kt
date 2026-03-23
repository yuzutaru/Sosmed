package com.yustar.dashboard.domain.model

data class Post(
    val id: String,
    val createdAt: String,
    val content: String?,
    val userId: String?,
    val postMedia: List<PostMedia>,
    val postProfile: PostProfile?
)

data class PostMedia(
    val id: String,
    val postId: String,
    val url: String,
    val mediaType: String?
)

data class PostProfile(
    val firstName: String,
    val lastName: String
)
