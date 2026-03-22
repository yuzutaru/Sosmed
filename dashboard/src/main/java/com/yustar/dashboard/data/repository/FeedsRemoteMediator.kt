package com.yustar.dashboard.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.local.entity.PostEntity
import com.yustar.dashboard.data.local.entity.PostMediaEntity
import com.yustar.dashboard.data.local.entity.PostProfileEntity
import com.yustar.dashboard.data.local.entity.RemoteKeyEntity
import com.yustar.dashboard.data.local.model.PostWithMedia
import com.yustar.dashboard.data.remote.FeedsApi
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class FeedsRemoteMediator(
    private val api: FeedsApi,
    private val usersApi: UsersApi,
    private val database: FeedsDatabase,
    private val sessionManager: SessionManager
) : RemoteMediator<Int, PostWithMedia>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostWithMedia>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 0
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            var accessToken = sessionManager.getAccessToken() ?: ""
            val offset = page * state.config.pageSize

            val response = try {
                api.getFeedsPaged(
                    authorization = "Bearer $accessToken",
                    limit = state.config.pageSize,
                    offset = offset
                )
            } catch (e: HttpException) {
                if (e.code() == 401 || e.code() == 403) {
                    val refreshToken = sessionManager.getRefreshToken()
                    if (refreshToken != null) {
                        val refreshResponse = usersApi.refreshToken(
                            refreshTokenRequest = RefreshTokenRequest(refreshToken)
                        )
                        sessionManager.updateAccessToken(refreshResponse.accessToken)
                        accessToken = refreshResponse.accessToken

                        api.getFeedsPaged(
                            authorization = "Bearer $accessToken",
                            limit = state.config.pageSize,
                            offset = offset
                        )
                    } else {
                        throw e
                    }
                } else {
                    throw e
                }
            }

            val endOfPaginationReached = response.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().clearRemoteKeys()
                    database.postDao().clearPosts()
                    database.postDao().clearMedia()
                    database.postDao().clearProfile()
                }
                val prevKey = if (page == 0) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = response.map {
                    RemoteKeyEntity(postId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.remoteKeyDao().insertAll(keys)

                val postEntities = response.map {
                    PostEntity(
                        id = it.id,
                        createdAt = it.createdAt,
                        content = it.content,
                        userId = it.userId
                    )
                }
                database.postDao().insertPosts(postEntities)

                val mediaEntities = response.flatMap { postDto ->
                    postDto.postMedia?.map { mediaDto ->
                        PostMediaEntity(
                            id = mediaDto.id,
                            postId = mediaDto.postId,
                            url = mediaDto.url,
                            mediaType = mediaDto.mediaType
                        )
                    } ?: emptyList()
                }
                database.postDao().insertMedia(mediaEntities)

                val profileEntities = response.flatMap { postDto ->
                    postDto.profiles?. { profileDto ->
                        PostProfileEntity(
                            userId = profileDto.id,
                            postId = postDto.id,
                            firstName = profileDto.firstName ?: "",
                            lastName = profileDto.lastName ?: ""
                        )
                    }
                }
                database.postDao().insertProfile(profileEntities)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostWithMedia>): RemoteKeyEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { postWithMedia ->
                database.remoteKeyDao().remoteKeysPostId(postWithMedia.post.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PostWithMedia>): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { postWithMedia ->
                database.remoteKeyDao().remoteKeysPostId(postWithMedia.post.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostWithMedia>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.post?.id?.let { postId ->
                database.remoteKeyDao().remoteKeysPostId(postId)
            }
        }
    }
}
