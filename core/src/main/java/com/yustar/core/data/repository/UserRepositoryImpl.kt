package com.yustar.core.data.repository

import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.remote.model.ResponseHandler

class UserRepositoryImpl(
    private val api: UsersApi, private val responseHandler: ResponseHandler
) : UserRepository {

    override suspend fun authRegister(authRequest: AuthRequest): Resource<AuthResponse> {
        try {
            val response = api.authRegister(authRequest)
            return responseHandler.handleSuccess(response)

        } catch (e: Exception) {
            return responseHandler.handleException(e)
        }
    }
}