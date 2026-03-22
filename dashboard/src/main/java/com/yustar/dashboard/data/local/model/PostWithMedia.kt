package com.yustar.dashboard.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.yustar.dashboard.data.local.entity.PostEntity
import com.yustar.dashboard.data.local.entity.PostMediaEntity

data class PostWithMedia(
    @Embedded val post: PostEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "postId"
    )
    val media: List<PostMediaEntity>
)
