package com.yustar.dashboard.data.repository

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.paging.PagingData
import com.yustar.dashboard.data.remote.model.MediaDto
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.model.MediaType
import com.yustar.dashboard.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedsRepository {
    fun getFeedsPaged(): Flow<PagingData<Post>>
    @OptIn(ExperimentalFoundationApi::class)
    fun getLocalImages(bucketId: String? = null, type: MediaType): Flow<List<LocalMedia>>
    fun getLocalAlbums(): Flow<List<AlbumItem>>
    suspend fun getSignedUploadUrl(): Pair<String, String>
    suspend fun uploadFile(path: String, token: String, bytes: ByteArray)
    fun getPublicUrl(path: String): String
    suspend fun createPost(caption: String, location: String, media: List<MediaDto>)
}