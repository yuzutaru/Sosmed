package com.yustar.core.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Yustar Pramudana on 19/03/26.
 */

data class ProfileRequest(
    @SerializedName("id")
    val id: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)
