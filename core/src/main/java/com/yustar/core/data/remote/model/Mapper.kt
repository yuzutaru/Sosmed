package com.yustar.core.data.remote.model

import com.yustar.core.domain.model.Profile
import com.yustar.core.domain.model.User

fun AuthResponse.toDomain(): User {
    return User(
        id = this.id,
        email = this.email,
        username = this.userMetadata.username
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
