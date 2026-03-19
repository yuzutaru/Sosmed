package com.yustar.auth.presentation.state

/**
 * Created by Yustar Pramudana on 07/03/26.
 */

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)
