package com.yustar.auth.domain

import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.ProfileRequest
import com.yustar.core.data.remote.model.RegisterData
import com.yustar.core.data.remote.model.Status
import com.yustar.core.data.repository.UserRepository

class RegisterUserUseCase(
    private val repository: UserRepository
) {

    suspend operator fun invoke(dto: RegisterDto) {
        val authRequest = AuthRequest(
            email = dto.username,
            password = dto.password,
            data = RegisterData(username = dto.username)
        )

        val authResource = repository.authRegister(authRequest)

        if (authResource.status == Status.SUCCESS) {
            val authResponse = authResource.data
            if (authResponse != null) {
                val profileRequest = ProfileRequest(
                    id = authResponse.id,
                    firstName = dto.firstName,
                    lastName = dto.lastName,
                    address = dto.address,
                    phoneNumber = dto.phoneNumber
                )
                val profileResource = repository.profileSignUp(profileRequest)
                if (profileResource.status == Status.ERROR) {
                    throw Exception(profileResource.message ?: "Failed to create profile")
                }
            } else {
                throw Exception("Registration failed: No response data")
            }
        } else {
            throw Exception(authResource.message ?: "Registration failed")
        }
    }
}
