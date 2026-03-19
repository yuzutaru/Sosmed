package com.yustar.auth.presentation.event

/**
 * Created by Yustar Pramudana on 08/03/26.
 */

sealed class RegisterUiEvent {
    data class OnFirstNameChanged(val firstName: String): RegisterUiEvent()
    data class OnLastNameChanged(val lastName: String): RegisterUiEvent()
    data class OnAddressChanged(val address: String): RegisterUiEvent()
    data class OnPhoneNumberChanged(val phoneNumber: String): RegisterUiEvent()
    data class OnUsernameChanged(val username: String): RegisterUiEvent()
    data class OnPasswordChanged(val password: String): RegisterUiEvent()
}