package com.yustar.auth.domain

import com.yustar.core.session.SessionManager
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Created by Yustar Pramudana on 09/03/26.
 */

class LogoutUseCaseTest {

    private val sessionManager: SessionManager = mockk(relaxed = true)
    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setUp() {
        logoutUseCase = LogoutUseCase(sessionManager)
    }

    @Test
    fun `when invoke is called, logout session`() = runTest {
        // When
        logoutUseCase()

        // Then
        coVerify { sessionManager.logout() }
    }
}
