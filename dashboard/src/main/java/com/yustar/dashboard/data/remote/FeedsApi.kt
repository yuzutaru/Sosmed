package com.yustar.dashboard.data.remote

import com.yustar.core.BuildConfig
import com.yustar.dashboard.data.remote.model.PostResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * Created by Yustar Pramudana on 21/03/26.
 */

interface FeedsApi {
    @GET("rest/v1/posts")
    suspend fun getFeedsPaged(
        @Query("select") select: String = "*,post_media(*)",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int
    ): List<PostResponseDto>
}
