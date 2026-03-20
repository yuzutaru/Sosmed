package com.yustar.core.data.remote.model

import com.yustar.core.domain.model.AuthToken
import com.yustar.core.domain.model.Profile
import com.yustar.core.domain.model.User

fun AuthResponse.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        username = this.userMetadata.username
    )
}

fun LoginResponse.toDomain(): AuthToken {
    return AuthToken(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        tokenType = this.tokenType,
        expiresIn = this.expiresIn,
        user = this.user.toDomain()
    )
}

fun RefreshTokenResponse.toDomain(): AuthToken {
    return AuthToken(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        tokenType = this.tokenType,
        expiresIn = this.expiresIn,
        user = User(
            id = this.user.id,
            email = this.user.email,
            username = "" // RefreshTokenUser doesn't have username, maybe fetch it later or keep it empty
        )
    )
}

fun ProfileRequest.toDomain(): Profile {
    return Profile(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        phoneNumber = this.phoneNumber
    )
}

fun Profile.toRequest(): ProfileRequest {
    return ProfileRequest(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        phoneNumber = this.phoneNumber
    )
}

fun UpdateProfileRequest.toDomain(id: String): Profile {
    return Profile(
        id = id,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        phoneNumber = this.phoneNumber
    )
}

fun Profile.toUpdateRequest(): UpdateProfileRequest {
    return UpdateProfileRequest(
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        phoneNumber = this.phoneNumber
    )
}
