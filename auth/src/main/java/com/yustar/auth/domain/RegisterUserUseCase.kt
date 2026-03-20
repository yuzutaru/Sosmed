package com.yustar.auth.domain

import com.yustar.auth.domain.model.RegisterUserParams
import com.yustar.core.data.remote.model.AuthRequest
import com.yustar.core.data.remote.model.ProfileRequest
import com.yustar.core.data.remote.model.RegisterData
import com.yustar.core.data.remote.model.Status
import com.yustar.core.data.repository.UserRepository

class RegisterUserUseCase(
    private val repository: UserRepository
) {

    suspend operator fun invoke(params: RegisterUserParams) {
        val authRequest = AuthRequest(
            email = params.username,
            password = params.password,
            data = RegisterData(username = params.username)
        )

        val authResource = repository.authRegister(authRequest)

        if (authResource.status == Status.SUCCESS) {
            val authResponse = authResource.data
            if (authResponse != null) {
                val profileRequest = ProfileRequest(
                    id = authResponse.id,
                    firstName = params.firstName,
                    lastName = params.lastName,
                    address = params.address,
                    phoneNumber = params.phoneNumber
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
