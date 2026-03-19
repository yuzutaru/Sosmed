package com.yustar.auth.presentation.state

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String = ""
)
