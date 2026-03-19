package com.yustar.auth.presentation.event

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

sealed class LoginUiEvent {
    data class OnEmailChanged(val email: String): LoginUiEvent()
    data class OnPasswordChanged(val password: String): LoginUiEvent()
}