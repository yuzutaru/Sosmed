package com.yustar.core.data.remote

import com.yustar.core.BuildConfig
import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.ProfileRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

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

    @POST("rest/v1/profiles")
    suspend fun profileSignUp(
        @Body profileRequest: ProfileRequest,
        @Header("apikey") apiKey: String = BuildConfig.SUPABASE_KEY,
        @Header("Authorization") authorization: String = "Bearer ${BuildConfig.SUPABASE_KEY}",
        @Header("Prefer") prefer: String = "return=minimal"
    )
}
