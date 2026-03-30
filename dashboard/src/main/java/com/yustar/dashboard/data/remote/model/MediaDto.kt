package com.yustar.dashboard.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaDto(
    val media_url: String,
    val media_type: String,
    val position: Int
)