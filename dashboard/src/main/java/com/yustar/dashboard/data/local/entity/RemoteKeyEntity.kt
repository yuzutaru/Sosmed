package com.yustar.dashboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey
    val postId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
