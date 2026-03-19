package com.yustar.auth.domain

import com.yustar.core.data.repository.UserRepository

class RegisterUserUseCase(
    private val repository: UserRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String,
        firstName: String = "",
        lastName: String = "",
        address: String = "",
        phoneNumber: String = ""
    ) {
        // Check if user already exists
        if (repository.getUser(username) != null) {
            throw Exception("User with this username/email is already registered")
        }

        repository.register(
            username = username,
            password = password,
            firstName = firstName,
            lastName = lastName,
            address = address,
            phoneNumber = phoneNumber
        )
    }
}