package com.yustar.dashboard.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.yustar.dashboard.data.local.entity.PostEntity
import com.yustar.dashboard.data.local.entity.PostMediaEntity
import com.yustar.dashboard.data.local.entity.PostProfileEntity

data class PostWithMedia(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val media: List<PostMediaEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val profile: PostProfileEntity
)
