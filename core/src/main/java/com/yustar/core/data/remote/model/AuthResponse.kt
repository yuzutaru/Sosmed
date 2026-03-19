package com.yustar.core.data.remote.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("aud")
    val aud: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_confirmed_at")
    val emailConfirmedAt: String?,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("confirmation_sent_at")
    val confirmationSentAt: String?,
    @SerializedName("confirmed_at")
    val confirmedAt: String?,
    @SerializedName("last_sign_in_at")
    val lastSignInAt: String?,
    @SerializedName("app_metadata")
    val appMetadata: AppMetadata,
    @SerializedName("user_metadata")
    val userMetadata: UserMetadata,
    @SerializedName("identities")
    val identities: List<Identity>,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("is_anonymous")
    val isAnonymous: Boolean
)

data class AppMetadata(
    @SerializedName("provider")
    val provider: String,
    @SerializedName("providers")
    val providers: List<String>
)

data class UserMetadata(
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified")
    val emailVerified: Boolean,
    @SerializedName("phone_verified")
    val phoneVerified: Boolean,
    @SerializedName("sub")
    val sub: String,
    @SerializedName("username")
    val username: String
)

data class Identity(
    @SerializedName("identity_id")
    val identityId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("identity_data")
    val identityData: IdentityData,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("last_sign_in_at")
    val lastSignInAt: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("email")
    val email: String
)

data class IdentityData(
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified")
    val emailVerified: Boolean,
    @SerializedName("phone_verified")
    val phoneVerified: Boolean,
    @SerializedName("sub")
    val sub: String,
    @SerializedName("username")
    val username: String
)
