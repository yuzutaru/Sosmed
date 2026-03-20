package com.yustar.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yustar.auth.domain.RegisterDto
import com.yustar.auth.domain.RegisterUserUseCase
import com.yustar.auth.presentation.event.RegisterUiEvent
import com.yustar.auth.presentation.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Yustar Pramudana on 07/03/26.
 */

class RegisterViewModel(private val registerUserUseCase: RegisterUserUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.OnAddressChanged -> _uiState.update { it.copy(address = event.address) }
            is RegisterUiEvent.OnFirstNameChanged -> _uiState.update { it.copy(firstName = event.firstName) }
            is RegisterUiEvent.OnLastNameChanged -> _uiState.update { it.copy(lastName = event.lastName) }
            is RegisterUiEvent.OnPasswordChanged -> _uiState.update { it.copy(password = event.password) }
            is RegisterUiEvent.OnPhoneNumberChanged -> _uiState.update { it.copy(phoneNumber = event.phoneNumber) }
            is RegisterUiEvent.OnUsernameChanged -> _uiState.update { it.copy(username = event.username) }
        }
    }

    fun register() {
        if (_uiState.value.username.isEmpty() || _uiState.value.password.isEmpty()) {
            _uiState.update { it.copy(error = "Email and password are required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = "") }
            try {
                val dto = RegisterDto(
                    username = _uiState.value.username,
                    password = _uiState.value.password,
                    firstName = _uiState.value.firstName,
                    lastName = _uiState.value.lastName,
                    address = _uiState.value.address,
                    phoneNumber = _uiState.value.phoneNumber
                )
                registerUserUseCase(dto)
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
    
    fun resetSuccessState() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = "") }
    }
}
