package com.yustar.auth.domain

import com.yustar.auth.domain.model.RegisterUserParams
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RegisterUserUseCaseTest {

    private val repository: UserRepository = mockk()
    private lateinit var registerUserUseCase: RegisterUserUseCase

    @Before
    fun setUp() {
        registerUserUseCase = RegisterUserUseCase(repository)
    }

    @Test
    fun `when invoke is called and auth is successful, it should call profileSignUp`() = runTest {
        // Given
        val params = RegisterUserParams(
            username = "testUser",
            password = "password123",
            firstName = "John",
            lastName = "Doe",
            address = "123 Main St",
            phoneNumber = "555-1234"
        )
        val userId = "user-id-123"

        val authResponse = mockk<AuthResponse> {
            coEvery { id } returns userId
        }

        coEvery { repository.authRegister(any()) } returns Resource.success(authResponse)
        coEvery { repository.profileSignUp(any()) } returns Resource.success(Unit)

        // When
        registerUserUseCase(params)

        // Then
        coVerify { repository.authRegister(match {
            it.email == params.username && it.password == params.password && it.data.username == params.username
        }) }
        coVerify { repository.profileSignUp(match {
            it.id == userId && it.firstName == params.firstName && it.lastName == params.lastName &&
            it.address == params.address && it.phoneNumber == params.phoneNumber
        }) }
    }

    @Test
    fun `when invoke is called and auth fails, it should throw Exception and not call profileSignUp`() = runTest {
        // Given
        val params = RegisterUserParams(
            username = "testUser",
            password = "password123"
        )
        val errorMessage = "Auth failed"

        coEvery { repository.authRegister(any()) } returns Resource.error(null, errorMessage)

        // When - Use runCatching to capture the exception within the coroutine
        val result = runCatching {
            registerUserUseCase(params)
        }
        val exception = result.exceptionOrNull()

        // Then
        assertEquals(errorMessage, exception?.message)
        coVerify { repository.authRegister(any()) }
        coVerify(exactly = 0) { repository.profileSignUp(any()) }
    }

    @Test
    fun `when invoke is called and auth succeeds but profileSignUp fails, it should throw Exception`() = runTest {
        // Given
        val params = RegisterUserParams(
            username = "testUser",
            password = "password123"
        )
        val userId = "user-id-123"
        val errorMessage = "Profile creation failed"

        val authResponse = mockk<AuthResponse> {
            coEvery { id } returns userId
        }

        coEvery { repository.authRegister(any()) } returns Resource.success(authResponse)
        coEvery { repository.profileSignUp(any()) } returns Resource.error(null, errorMessage)

        // When
        val result = runCatching {
            registerUserUseCase(params)
        }
        val exception = result.exceptionOrNull()

        // Then
        assertEquals(errorMessage, exception?.message)
        coVerify { repository.authRegister(any()) }
        coVerify { repository.profileSignUp(any()) }
    }
}
