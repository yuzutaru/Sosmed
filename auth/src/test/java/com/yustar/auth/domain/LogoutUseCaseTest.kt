package com.yustar.auth.domain

import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Created by Yustar Pramudana on 09/03/26.
 */

class LogoutUseCaseTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        logoutUseCase = LogoutUseCase(userRepository, sessionManager)
    }

    @Test
    fun `when invoke is called with token and logout success, call sessionManager logout`() = runTest {
        // Given
        val token = "test_token"
        every { sessionManager.getAccessToken() } returns token
        coEvery { userRepository.logout(token) } returns Resource.success(Unit)

        // When
        logoutUseCase()

        // Then
        coVerify { userRepository.logout(token) }
        coVerify { sessionManager.logout() }
    }

    @Test
    fun `when invoke is called with token and logout fails, do not call sessionManager logout`() = runTest {
        // Given
        val token = "test_token"
        every { sessionManager.getAccessToken() } returns token
        coEvery { userRepository.logout(token) } returns Resource.error(null, "Error")

        // When
        logoutUseCase()

        // Then
        coVerify { userRepository.logout(token) }
        coVerify(exactly = 0) { sessionManager.logout() }
    }

    @Test
    fun `when invoke is called without token, call sessionManager logout`() = runTest {
        // Given
        every { sessionManager.getAccessToken() } returns null

        // When
        logoutUseCase()

        // Then
        coVerify(exactly = 0) { userRepository.logout(any()) }
        coVerify { sessionManager.logout() }
    }
}
