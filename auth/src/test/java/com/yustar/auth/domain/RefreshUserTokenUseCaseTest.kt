package com.yustar.auth.domain

import com.yustar.core.data.remote.model.RefreshTokenRequest
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.RefreshTokenUser
import com.yustar.core.data.remote.model.Resource
import com.yustar.core.data.repository.UserRepository
import com.yustar.core.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by Yustar Pramudana on 09/03/26.
 */

class RefreshUserTokenUseCaseTest {

    private val repository: UserRepository = mockk()
    private val session: SessionManager = mockk(relaxed = true)
    private lateinit var refreshUserTokenUseCase: RefreshUserTokenUseCase

    @Before
    fun setUp() {
        refreshUserTokenUseCase = RefreshUserTokenUseCase(repository, session)
    }

    @Test
    fun `when refresh token is null, should return false`() = runTest {
        // Given
        every { session.getRefreshToken() } returns null

        // When
        val result = refreshUserTokenUseCase()

        // Then
        assertFalse(result)
        coVerify(exactly = 0) { repository.refreshToken(any()) }
    }

    @Test
    fun `when refresh token is successful, should update access token and return true`() = runTest {
        // Given
        val oldRefreshToken = "old_refresh_token"
        val newAccessToken = "new_access_token"
        val refreshTokenResponse = RefreshTokenResponse(
            accessToken = newAccessToken,
            tokenType = "Bearer",
            expiresIn = 3600,
            refreshToken = "new_refresh_token",
            user = RefreshTokenUser("user123", "test@example.com")
        )

        every { session.getRefreshToken() } returns oldRefreshToken
        coEvery { repository.refreshToken(RefreshTokenRequest(oldRefreshToken)) } returns Resource.success(refreshTokenResponse)

        // When
        val result = refreshUserTokenUseCase()

        // Then
        assertTrue(result)
        coVerify { session.updateAccessToken(newAccessToken) }
    }

    @Test
    fun `when refresh token fails, should return false`() = runTest {
        // Given
        val oldRefreshToken = "old_refresh_token"
        every { session.getRefreshToken() } returns oldRefreshToken
        coEvery { repository.refreshToken(any()) } returns Resource.error(null, "Token expired")

        // When
        val result = refreshUserTokenUseCase()

        // Then
        assertFalse(result)
        coVerify(exactly = 0) { session.updateAccessToken(any()) }
    }
}
