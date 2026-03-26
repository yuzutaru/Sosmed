package com.yustar.dashboard.domain.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.data.remote.model.CreatePostMediaDto
import com.yustar.dashboard.data.remote.model.CreatePostRequestDto
import com.yustar.dashboard.data.repository.FeedsRemoteMediator
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.model.PostProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FeedsRepositoryImpl(
    private val context: Context,
    private val api: FeedsApi,
    private val usersApi: UsersApi,
    private val database: FeedsDatabase,
    private val sessionManager: SessionManager
) : FeedsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getFeedsPaged(): Flow<PagingData<Post>> {
        val pagingSourceFactory = { database.postDao().getPostsPaged() }

        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            remoteMediator = FeedsRemoteMediator(
                api = api,
                usersApi = usersApi,
                database = database,
                sessionManager = sessionManager
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { postWithMedia ->
                Post(
                    id = postWithMedia.post.id,
                    createdAt = postWithMedia.post.createdAt,
                    content = postWithMedia.post.content,
                    userId = postWithMedia.post.userId,
                    postMedia = postWithMedia.media.map {
                        PostMedia(
                            id = it.id,
                            postId = it.postId,
                            url = it.url,
                            mediaType = it.mediaType
                        )
                    },
                    postProfile = postWithMedia.profile?.let {
                        PostProfile(
                            firstName = it.firstName,
                            lastName = it.lastName
                        )
                    }
                )
            }
        }
    }

    override suspend fun createPost(
        caption: String,
        location: String,
        media: List<PostMedia>
    ): Resource<Unit> {
        var token = sessionManager.getAccessToken() ?: return Resource.error(null, "Token not found")
        val request = CreatePostRequestDto(
            caption = caption,
            location = location,
            media = media.mapIndexed { index, postMedia ->
                CreatePostMediaDto(
                    mediaUrl = postMedia.url,
                    mediaType = postMedia.mediaType ?: "image",
                    position = index + 1
                )
            }
        )
        return try {
            var response = api.createPost(
                authorization = "Bearer $token",
                request = request
            )

            if (response.code() == 401 || response.code() == 403) {
                val refreshToken = sessionManager.getRefreshToken()
                if (refreshToken != null) {
                    val refreshResponse = usersApi.refreshToken(
                        refreshTokenRequest = RefreshTokenRequest(refreshToken)
                    )
                    sessionManager.saveTokens(
                        refreshResponse.accessToken,
                        refreshResponse.refreshToken
                    )
                    token = refreshResponse.accessToken

                    response = api.createPost(
                        authorization = "Bearer $token",
                        request = request
                    )
                }
            }

            if (response.code() == 204) {
                Resource.success(Unit)
            } else {
                Resource.error(null, response.message())
            }
        } catch (e: Exception) {
            Resource.error(null, e.localizedMessage)
        }
    }

    override fun getLocalImages(bucketId: String?): Flow<List<LocalMedia>> = flow {
        val images = mutableListOf<LocalMedia>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val selection = if (bucketId != null) "${MediaStore.Images.Media.BUCKET_ID} = ?" else null
        val selectionArgs = if (bucketId != null) arrayOf(bucketId) else null
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val dateAdded = cursor.getLong(dateAddedColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                images.add(LocalMedia(id, uri, name, dateAdded))
            }
        }
        emit(images)
    }

    override fun getLocalAlbums(): Flow<List<AlbumItem>> = flow {
        val albums = mutableListOf<AlbumItem>()
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val albumMap = mutableMapOf<String, AlbumData>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val bucketId = cursor.getString(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val id = cursor.getLong(idColumn)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id).toString()

                val data = albumMap[bucketId]
                if (data == null) {
                    albumMap[bucketId] = AlbumData(bucketName, 1, uri)
                } else {
                    albumMap[bucketId] = data.copy(count = data.count + 1)
                }
            }
        }

        albumMap.forEach { (id, data) ->
            albums.add(AlbumItem(id, data.name, data.count.toString(), data.thumbnailUri))
        }

        // Add a "Recents" album at the beginning if not empty
        if (albums.isNotEmpty()) {
             // You might want to calculate total count for Recents or just leave it as is.
        }

        emit(albums.sortedByDescending { it.count.toInt() })
    }

    private data class AlbumData(val name: String, val count: Int, val thumbnailUri: String)
}
