package com.yustar.core.data.remote

import com.yustar.core.BuildConfig
import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.LoginRequest
import com.yustar.core.data.remote.model.LoginResponse
import com.yustar.core.data.remote.model.ProfileRequest
import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Yustar Pramudana on 19/03/26.
 */

interface UsersApi {

    @POST("auth/v1/signup")
    suspend fun authRegister(
        @Body authRequest: AuthRequest,
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String = "Bearer ${BuildConfig.SUPABASE_KEY}"
    ): AuthResponse

    @POST("auth/v1/token")
    suspend fun login(
        @Query("grant_type") grantType: String = "password",
        @Body loginRequest: LoginRequest,
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY
    ): LoginResponse

    @POST("auth/v1/token")
    suspend fun refreshToken(
        @Query("grant_type") grantType: String = "refresh_token",
        @Body refreshTokenRequest: RefreshTokenRequest,
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY
    ): RefreshTokenResponse

    @POST("auth/v1/logout")
    suspend fun logout(
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @POST("rest/v1/profiles")
    suspend fun profileSignUp(
        @Body profileRequest: ProfileRequest,
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String = "Bearer ${BuildConfig.SUPABASE_KEY}",
        @Header("Prefer") prefer: String = "return=minimal"
    ): Response<Unit>

    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Query("id") id: String,
        @Body updateProfileRequest: UpdateProfileRequest,
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String = "Bearer ${BuildConfig.SUPABASE_KEY}",
        @Header("Prefer") prefer: String = "return=minimal"
    ): Response<Unit>
}
