package com.yustar.core.data.repository

import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.Resource

interface UserRepository {
    suspend fun authRegister(authRequest: AuthRequest): Resource<AuthResponse>
}