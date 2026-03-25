package com.yustar.dashboard.domain.repository

import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.RefreshTokenUser
import com.yustar.core.data.remote.model.Status
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.domain.model.PostMedia
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FeedsRepositoryImplTest {

    private lateinit var api: FeedsApi
    private lateinit var usersApi: UsersApi
    private lateinit var database: FeedsDatabase
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: FeedsRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        usersApi = mockk()
        database = mockk()
        sessionManager = mockk(relaxed = true)
        repository = FeedsRepositoryImpl(api, usersApi, database, sessionManager)
    }

    @Test
    fun `createPost success returns Resource success`() = runTest {
        // Given
        val caption = "caption"
        val location = "location"
        val media = listOf(PostMedia("1", "1", "url", "image"))
        val token = "access_token"

        every { sessionManager.getAccessToken() } returns token
        coEvery { api.createPost(authorization = any(), request = any()) } returns Response.success(204, Unit)

        // When
        val result = repository.createPost(caption, location, media)

        // Then
        assertEquals(Status.SUCCESS, result.status)
        coVerify { api.createPost(authorization = "Bearer $token", request = any()) }
    }

    @Test
    fun `createPost returns error when token is missing`() = runTest {
        // Given
        every { sessionManager.getAccessToken() } returns null

        // When
        val result = repository.createPost("caption", "location", emptyList())

        // Then
        assertEquals(Status.ERROR, result.status)
        assertEquals("Token not found", result.message)
    }

    @Test
    fun `createPost retry success after token refresh`() = runTest {
        // Given
        val caption = "caption"
        val location = "location"
        val media = emptyList<PostMedia>()
        val oldToken = "old_token"
        val refreshToken = "refresh_token"
        val newToken = "new_token"

        every { sessionManager.getAccessToken() } returns oldToken
        every { sessionManager.getRefreshToken() } returns refreshToken
        
        // First call fails with 401
        coEvery { 
            api.createPost(authorization = "Bearer $oldToken", request = any()) 
        } returns Response.error(401, "".toResponseBody())

        // Refresh token succeeds
        coEvery { 
            usersApi.refreshToken(grantType = any(), refreshTokenRequest = any(), apiKey = any()) 
        } returns RefreshTokenResponse(
            accessToken = newToken,
            refreshToken = "new_refresh_token",
            tokenType = "bearer",
            expiresIn = 3600,
            user = RefreshTokenUser("id", "email")
        )

        // Second call succeeds
        coEvery { 
            api.createPost(authorization = "Bearer $newToken", request = any()) 
        } returns Response.success(204, Unit)

        // When
        val result = repository.createPost(caption, location, media)

        // Then
        assertEquals(Status.SUCCESS, result.status)
        coVerify { sessionManager.saveTokens(newToken, "new_refresh_token") }
        coVerify(exactly = 1) { api.createPost(authorization = "Bearer $oldToken", request = any()) }
        coVerify(exactly = 1) { api.createPost(authorization = "Bearer $newToken", request = any()) }
    }

    @Test
    fun `createPost returns error when api throws exception`() = runTest {
        // Given
        val errorMessage = "Network Error"
        every { sessionManager.getAccessToken() } returns "token"
        coEvery { api.createPost(authorization = any(), request = any()) } throws Exception(errorMessage)

        // When
        val result = repository.createPost("caption", "location", emptyList())

        // Then
        assertEquals(Status.ERROR, result.status)
        assertEquals(errorMessage, result.message)
    }
}
