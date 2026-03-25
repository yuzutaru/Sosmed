package com.yustar.dashboard.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Yustar Pramudana on 21/03/26.
 */

data class CreatePostRequestDto(
    @SerializedName("p_caption")
    val caption: String,
    @SerializedName("p_location")
    val location: String,
    @SerializedName("p_media")
    val media: List<CreatePostMediaDto>
)

data class CreatePostMediaDto(
    @SerializedName("media_url")
    val mediaUrl: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("position")
    val position: Int
)
