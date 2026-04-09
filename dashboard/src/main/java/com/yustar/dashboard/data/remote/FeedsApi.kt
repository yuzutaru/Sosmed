package com.yustar.dashboard.data.remote

import com.yustar.core.BuildConfig
import com.yustar.dashboard.data.remote.model.CreatePostRequestDto
import com.yustar.dashboard.data.remote.model.LocationDto
import com.yustar.dashboard.data.remote.model.PostResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by Yustar Pramudana on 21/03/26.
 */

interface FeedsApi {
    @GET("rest/v1/posts")
    suspend fun getFeedsPaged(
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String,
        @Query("select") select: String = "*,profiles(*),post_media(*)",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int
    ): List<PostResponseDto>

    @POST("rest/v1/rpc/create_post_with_media")
    suspend fun createPost(
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String,
        @Body request: CreatePostRequestDto
    ): Response<Unit>

    @GET
    suspend fun getLocations(
        @Url url: String = "https://nominatim.openstreetmap.org/search",
        @Header("User-Agent") userAgent: String = "MyApp/1.0",
        @Query("q") query: String,
        @Query("countrycodes") countryCodes: String = "id",
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5
    ): List<LocationDto>
}
