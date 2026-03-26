package com.yustar.dashboard.domain.model

import android.net.Uri

data class LocalMedia(
    val id: Long,
    val uri: Uri,
    val name: String,
    val dateAdded: Long,
    val isVideo: Boolean = false,
    val duration: String? = null
)
