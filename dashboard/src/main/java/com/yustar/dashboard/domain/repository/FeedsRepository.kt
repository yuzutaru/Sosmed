package com.yustar.dashboard.domain.repository

import androidx.paging.PagingData
import com.yustar.core.data.remote.model.Resource
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import kotlinx.coroutines.flow.Flow

interface FeedsRepository {
    fun getFeedsPaged(): Flow<PagingData<Post>>
    suspend fun createPost(caption: String, location: String, media: List<PostMedia>): Resource<Unit>
    fun getLocalImages(bucketId: String? = null): Flow<List<LocalMedia>>
    fun getLocalAlbums(): Flow<List<AlbumItem>>
}
