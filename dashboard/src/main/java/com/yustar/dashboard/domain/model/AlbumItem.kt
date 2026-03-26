package com.yustar.dashboard.domain.model

data class AlbumItem(
    val id: String,
    val name: String,
    val count: String,
    val thumbnailUri: String?,
    val isVideo: Boolean = false,
    val duration: String? = null
)
