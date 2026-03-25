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

    override fun getLocalImages(): Flow<List<LocalMedia>> = flow {
        val images = mutableListOf<LocalMedia>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
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
}
