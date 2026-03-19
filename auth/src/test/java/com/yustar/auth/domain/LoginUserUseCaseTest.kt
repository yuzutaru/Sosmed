package com.yustar.auth.domain

import com.yustar.core.data.local.User
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoginUserUseCaseTest {

    private val repository: UserRepository = mockk()
    private val session: SessionManager = mockk(relaxed = true)
    private lateinit var loginUserUseCase: LoginUserUseCase

    @Before
    fun setUp() {
        loginUserUseCase = LoginUserUseCase(repository, session)
    }

    @Test
    fun `when login is called with correct credentials, return Success and login session`() = runTest {
        // Given
        val username = "testUser"
        val password = "password123"
        val user = mockk<User>()
        coEvery { repository.getUser(username) } returns user
        coEvery { repository.login(username, password) } returns true

        // When
        val result = loginUserUseCase(username, password)

        // Then
        assertEquals(LoginResult.Success, result)
        coVerify { repository.login(username, password) }
        coVerify { session.login(username) }
    }

    @Test
    fun `when login is called with wrong password, return InvalidPassword and do not login session`() = runTest {
        // Given
        val username = "testUser"
        val password = "wrongPassword"
        val user = mockk<User>()
        coEvery { repository.getUser(username) } returns user
        coEvery { repository.login(username, password) } returns false

        // When
        val result = loginUserUseCase(username, password)

        // Then
        assertEquals(LoginResult.InvalidPassword, result)
        coVerify { repository.login(username, password) }
        coVerify(exactly = 0) { session.login(any()) }
    }

    @Test
    fun `when login is called with non-existent user, return UserNotFound`() = runTest {
        // Given
        val username = "nonExistent"
        val password = "anyPassword"
        coEvery { repository.getUser(username) } returns null

        // When
        val result = loginUserUseCase(username, password)

        // Then
        assertEquals(LoginResult.UserNotFound, result)
        coVerify(exactly = 0) { repository.login(any(), any()) }
        coVerify(exactly = 0) { session.login(any()) }
    }
}
