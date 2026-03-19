package com.yustar.auth.domain

import com.yustar.core.data.remote.model.AppMetadata
import com.yustar.core.data.remote.model.AuthResponse
import com.yustar.core.data.remote.model.LoginRequest
import com.yustar.core.data.remote.model.LoginResponse
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.remote.model.UserMetadata
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Yustar Pramudana on 09/03/26.
 */

class LoginUserUseCaseTest {

    private val repository: UserRepository = mockk()
    private val session: SessionManager = mockk(relaxed = true)
    private lateinit var loginUserUseCase: LoginUserUseCase

    @Before
    fun setUp() {
        loginUserUseCase = LoginUserUseCase(repository, session)
    }

    @Test
    fun `when login is successful, should save session and return Success`() = runTest {
        // Given
        val username = "test@example.com"
        val password = "password123"
        val loginRequest = LoginRequest(username, password)
        val authResponse = AuthResponse(
            id = "user123",
            aud = "authenticated",
            role = "authenticated",
            email = username,
            emailConfirmedAt = null,
            phone = "",
            confirmationSentAt = null,
            confirmedAt = null,
            lastSignInAt = null,
            appMetadata = AppMetadata("provider", emptyList()),
            userMetadata = UserMetadata(username, true, false, "sub", "testuser"),
            identities = emptyList(),
            createdAt = "",
            updatedAt = "",
            isAnonymous = false
        )
        val loginResponse = LoginResponse(
            accessToken = "access_token",
            tokenType = "Bearer",
            expiresIn = 3600,
            expiresAt = 0,
            refreshToken = "refresh_token",
            user = authResponse
        )
        coEvery { repository.login(loginRequest) } returns Resource.success(loginResponse)

        // When
        val result = loginUserUseCase(username, password)

        // Then
        assertEquals(LoginResult.Success, result)
        coVerify { session.login(username) }
        coVerify { session.saveTokens("access_token", "refresh_token") }
    }

    @Test
    fun `when login fails with invalid message, should return InvalidPassword`() = runTest {
        // Given
        val username = "test@example.com"
        val password = "wrong_password"
        coEvery { repository.login(any()) } returns Resource.error(null, "Invalid credentials")

        // When
        val result = loginUserUseCase(username, password)

        // Then
        assertEquals(LoginResult.InvalidPassword, result)
        coVerify(exactly = 0) { session.login(any()) }
    }

    @Test
    fun `when login fails with other message, should return UserNotFound`() = runTest {
        // Given
        val username = "unknown@example.com"
        val password = "password123"
        coEvery { repository.login(any()) } returns Resource.error(null, "Internal Server Error")

        // When
        val result = loginUserUseCase(username, password)

        // Then
        assertEquals(LoginResult.UserNotFound, result)
        coVerify(exactly = 0) { session.login(any()) }
    }
}
