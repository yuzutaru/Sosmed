package com.yustar.core.data.repository

import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.LoginRequest
import com.yustar.core.data.remote.model.LoginResponse
import com.yustar.core.data.remote.model.ProfileRequest
import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.remote.model.ResponseHandler
import com.yustar.core.data.remote.model.UpdateProfileRequest

class UserRepositoryImpl(
    private val api: UsersApi, private val responseHandler: ResponseHandler
) : UserRepository {

    override suspend fun authRegister(authRequest: AuthRequest): Resource<AuthResponse> {
        return try {
            val response = api.authRegister(authRequest)
            responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    override suspend fun login(loginRequest: LoginRequest): Resource<LoginResponse> {
        return try {
            val response = api.login(loginRequest = loginRequest)
            responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    override suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): Resource<RefreshTokenResponse> {
        return try {
            val response = api.refreshToken(refreshTokenRequest = refreshTokenRequest)
            responseHandler.handleSuccess(response)
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    override suspend fun logout(token: String): Resource<Unit> {
        return try {
            val response = api.logout(authorization = "Bearer $token")
            if (response.code() == 204) {
                responseHandler.handleResponse(response)
            } else {
                Resource.error(null, "Failed to logout")
            }
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    override suspend fun profileSignUp(profileRequest: ProfileRequest): Resource<Unit> {
        return try {
            val response = api.profileSignUp(profileRequest)
            if (response.code() == 201) {
                responseHandler.handleResponse(response)
            } else {
                Resource.error(null, "Failed to create profile")
            }
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

    override suspend fun updateProfile(
        id: String,
        updateProfileRequest: UpdateProfileRequest
    ): Resource<Unit> {
        return try {
            val response = api.updateProfile("eq.$id", updateProfileRequest)
            if (response.code() == 204) {
                responseHandler.handleResponse(response)
            } else {
                Resource.error(null, "Failed to update profile")
            }
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}
