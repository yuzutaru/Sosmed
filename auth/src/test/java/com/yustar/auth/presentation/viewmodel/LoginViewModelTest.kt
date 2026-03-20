package com.yustar.auth.presentation.viewmodel

import com.yustar.auth.domain.LoginResult
import com.yustar.auth.domain.LoginUserUseCase
import com.yustar.auth.presentation.event.LoginUiEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val loginUseCase: LoginUserUseCase = mockk()
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when email changes, uiState should be updated`() {
        val email = "test@example.com"
        viewModel.onEvent(LoginUiEvent.OnEmailChanged(email))
        Assert.assertEquals(email, viewModel.uiState.value.email)
    }

    @Test
    fun `when password changes, uiState should be updated`() {
        val password = "password123"
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged(password))
        Assert.assertEquals(password, viewModel.uiState.value.password)
    }

    @Test
    fun `when login is successful, onSuccess should be called`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        var successCalled = false

        viewModel.onEvent(LoginUiEvent.OnEmailChanged(email))
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged(password))

        coEvery { loginUseCase(email, password) } returns LoginResult.Success

        // When
        viewModel.login { successCalled = true }
        advanceUntilIdle()

        // Then
        Assert.assertTrue(successCalled)
        Assert.assertEquals("", viewModel.uiState.value.error)
    }

    @Test
    fun `when login fails with invalid password, error state should be updated`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrong_password"

        viewModel.onEvent(LoginUiEvent.OnEmailChanged(email))
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged(password))

        coEvery { loginUseCase(email, password) } returns LoginResult.InvalidPassword

        // When
        viewModel.login {}
        advanceUntilIdle()

        // Then
        Assert.assertEquals("Invalid password", viewModel.uiState.value.error)
    }

    @Test
    fun `when login fails because user not found, error state should be updated`() = runTest {
        // Given
        val email = "notfound@example.com"
        val password = "any_password"

        viewModel.onEvent(LoginUiEvent.OnEmailChanged(email))
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged(password))

        coEvery { loginUseCase(email, password) } returns LoginResult.UserNotFound

        // When
        viewModel.login {}
        advanceUntilIdle()

        // Then
        Assert.assertEquals("User not found. Please register first.", viewModel.uiState.value.error)
    }

    @Test
    fun `when email is empty, login should fail with error`() = runTest {
        // Given
        viewModel.onEvent(LoginUiEvent.OnEmailChanged(""))
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged("password123"))

        // When
        viewModel.login {}
        advanceUntilIdle()

        // Then
        Assert.assertEquals("Email and password are required", viewModel.uiState.value.error)
    }

    @Test
    fun `when password is empty, login should fail with error`() = runTest {
        // Given
        viewModel.onEvent(LoginUiEvent.OnEmailChanged("test@example.com"))
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged(""))

        // When
        viewModel.login {}
        advanceUntilIdle()

        // Then
        Assert.assertEquals("Email and password are required", viewModel.uiState.value.error)
    }

    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        viewModel.onEvent(LoginUiEvent.OnEmailChanged(""))
        viewModel.login {}
        Assert.assertEquals("Email and password are required", viewModel.uiState.value.error)

        // When
        viewModel.clearError()

        // Then
        Assert.assertEquals("", viewModel.uiState.value.error)
    }
}
