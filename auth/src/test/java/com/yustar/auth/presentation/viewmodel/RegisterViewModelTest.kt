package com.yustar.auth.presentation.viewmodel

import com.yustar.auth.domain.RegisterUserUseCase
import com.yustar.auth.presentation.event.RegisterUiEvent
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
class RegisterViewModelTest {

    private val registerUserUseCase: RegisterUserUseCase = mockk()
    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(registerUserUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when username changes, uiState should be updated`() {
        val username = "testuser"
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged(username))
        Assert.assertEquals(username, viewModel.uiState.value.username)
    }

    @Test
    fun `when password changes, uiState should be updated`() {
        val password = "password123"
        viewModel.onEvent(RegisterUiEvent.OnPasswordChanged(password))
        Assert.assertEquals(password, viewModel.uiState.value.password)
    }

    @Test
    fun `when first name changes, uiState should be updated`() {
        val firstName = "John"
        viewModel.onEvent(RegisterUiEvent.OnFirstNameChanged(firstName))
        Assert.assertEquals(firstName, viewModel.uiState.value.firstName)
    }

    @Test
    fun `when last name changes, uiState should be updated`() {
        val lastName = "Doe"
        viewModel.onEvent(RegisterUiEvent.OnLastNameChanged(lastName))
        Assert.assertEquals(lastName, viewModel.uiState.value.lastName)
    }

    @Test
    fun `when address changes, uiState should be updated`() {
        val address = "123 Main St"
        viewModel.onEvent(RegisterUiEvent.OnAddressChanged(address))
        Assert.assertEquals(address, viewModel.uiState.value.address)
    }

    @Test
    fun `when phone number changes, uiState should be updated`() {
        val phoneNumber = "555-1234"
        viewModel.onEvent(RegisterUiEvent.OnPhoneNumberChanged(phoneNumber))
        Assert.assertEquals(phoneNumber, viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun `when register is successful, isSuccess should be true`() = runTest {
        // Given
        val username = "testuser"
        val password = "password123"
        
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged(username))
        viewModel.onEvent(RegisterUiEvent.OnPasswordChanged(password))

        coEvery { 
            registerUserUseCase(any()) 
        } returns Unit

        // When
        viewModel.register()
        advanceUntilIdle()

        // Then
        Assert.assertTrue(viewModel.uiState.value.isSuccess)
        Assert.assertFalse(viewModel.uiState.value.isLoading)
        Assert.assertEquals("", viewModel.uiState.value.error)
    }

    @Test
    fun `when register fails, error message should be updated`() = runTest {
        // Given
        val errorMessage = "Registration failed"
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged("testuser"))
        viewModel.onEvent(RegisterUiEvent.OnPasswordChanged("password123"))
        
        coEvery { 
            registerUserUseCase(any()) 
        } throws Exception(errorMessage)

        // When
        viewModel.register()
        advanceUntilIdle()

        // Then
        Assert.assertFalse(viewModel.uiState.value.isSuccess)
        Assert.assertFalse(viewModel.uiState.value.isLoading)
        Assert.assertEquals(errorMessage, viewModel.uiState.value.error)
    }
    
    @Test
    fun `test resetSuccessState resets success flag`() = runTest {
        // Given
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged("testuser"))
        viewModel.onEvent(RegisterUiEvent.OnPasswordChanged("password123"))
        coEvery { 
            registerUserUseCase(any())
        } returns Unit

        viewModel.register()
        advanceUntilIdle()
        Assert.assertTrue(viewModel.uiState.value.isSuccess)

        // When
        viewModel.resetSuccessState()

        // Then
        Assert.assertFalse(viewModel.uiState.value.isSuccess)
    }

    @Test
    fun `when username is empty, register should fail with error`() = runTest {
        // Given
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged(""))
        viewModel.onEvent(RegisterUiEvent.OnPasswordChanged("password123"))

        // When
        viewModel.register()
        advanceUntilIdle()

        // Then
        Assert.assertEquals("Email and password are required", viewModel.uiState.value.error)
    }

    @Test
    fun `when password is empty, register should fail with error`() = runTest {
        // Given
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged("testuser"))
        viewModel.onEvent(RegisterUiEvent.OnPasswordChanged(""))

        // When
        viewModel.register()
        advanceUntilIdle()

        // Then
        Assert.assertEquals("Email and password are required", viewModel.uiState.value.error)
    }

    @Test
    fun `clearError should reset error state`() = runTest {
        // Given
        viewModel.onEvent(RegisterUiEvent.OnUsernameChanged(""))
        viewModel.register()
        Assert.assertEquals("Email and password are required", viewModel.uiState.value.error)

        // When
        viewModel.clearError()

        // Then
        Assert.assertEquals("", viewModel.uiState.value.error)
    }
}
