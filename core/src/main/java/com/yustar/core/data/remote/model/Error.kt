package com.yustar.core.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Yustar Pramudana on 05/11/22.
 */

data class Error(
    @SerializedName("error_type")
    var errorType: String? = null,
    @SerializedName("error_message")
    var errorMsg: String? = null,
    @SerializedName("detail")
    var detail: String? = null
)
