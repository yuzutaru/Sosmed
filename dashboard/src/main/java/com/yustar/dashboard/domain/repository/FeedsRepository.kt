package com.yustar.dashboard.domain.repository

import androidx.paging.PagingData
import com.yustar.dashboard.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedsRepository {
    fun getFeedsPaged(): Flow<PagingData<Post>>
}