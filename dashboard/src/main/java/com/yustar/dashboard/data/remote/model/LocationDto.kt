package com.yustar.dashboard.data.remote.model

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("place_id")
    val placeId: Long?,
    @SerializedName("licence")
    val licence: String?,
    @SerializedName("osm_type")
    val osmType: String?,
    @SerializedName("osm_id")
    val osmId: Long?,
    @SerializedName("boundingbox")
    val boundingBox: List<String>?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("lon")
    val lon: String?,
    @SerializedName("display_name")
    val displayName: String?,
    @SerializedName("class")
    val classType: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("importance")
    val importance: Double?
)
