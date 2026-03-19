package com.yustar.core.data.repository

import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.LoginRequest
import com.yustar.core.data.remote.model.LoginResponse
import com.yustar.core.data.remote.model.ProfileRequest
import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.remote.model.UpdateProfileRequest

interface UserRepository {
    suspend fun authRegister(authRequest: AuthRequest): Resource<AuthResponse>
    suspend fun login(loginRequest: LoginRequest): Resource<LoginResponse>
    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Resource<RefreshTokenResponse>
    suspend fun logout(token: String): Resource<Unit>
    suspend fun profileSignUp(profileRequest: ProfileRequest): Resource<Unit>
    suspend fun updateProfile(id: String, updateProfileRequest: UpdateProfileRequest): Resource<Unit>
}
