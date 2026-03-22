package com.yustar.dashboard.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yustar.core.data.remote.UsersApi
import com.yustar.core.data.remote.model.RefreshTokenResponse
import com.yustar.core.data.remote.model.RefreshTokenUser
import com.yustar.core.session.SessionManager
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.local.model.PostWithMedia
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.data.remote.model.PostResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class FeedsRemoteMediatorTest {

    private lateinit var database: FeedsDatabase
    private val api: FeedsApi = mockk()
    private val usersApi: UsersApi = mockk()
    private val sessionManager: SessionManager = mockk()
    private lateinit var remoteMediator: FeedsRemoteMediator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedsDatabase::class.java
        ).allowMainThreadQueries().build()
        
        every { sessionManager.getAccessToken() } returns "mock_token"
        every { sessionManager.getRefreshToken() } returns "mock_refresh_token"
        every { sessionManager.updateAccessToken(any()) } returns Unit

        remoteMediator = FeedsRemoteMediator(api, usersApi, database, sessionManager)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        // Given
        val mockResponse = listOf(
            PostResponseDto(
                id = "1",
                createdAt = "2023-01-01T00:00:00Z",
                content = "Post 1",
                userId = "user1",
                postMedia = emptyList()
            )
        )
        coEvery { 
            api.getFeedsPaged(
                apiKey = any(),
                authorization = any(),
                select = any(),
                order = any(),
                limit = any(),
                offset = any()
            ) 
        } returns mockResponse

        val pagingState = PagingState<Int, PostWithMedia>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // When
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
        // Given
        coEvery { 
            api.getFeedsPaged(
                apiKey = any(),
                authorization = any(),
                select = any(),
                order = any(),
                limit = any(),
                offset = any()
            ) 
        } returns emptyList()

        val pagingState = PagingState<Int, PostWithMedia>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // When
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runTest {
        // Given
        coEvery { 
            api.getFeedsPaged(
                apiKey = any(),
                authorization = any(),
                select = any(),
                order = any(),
                limit = any(),
                offset = any()
            ) 
        } throws Exception("API Error")

        val pagingState = PagingState<Int, PostWithMedia>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // When
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun loadReturnsSuccessWhenTokenIsRefreshedAfter401() = runTest {
        // Given
        val unauthorizedException = HttpException(
            Response.error<Any>(401, "".toResponseBody(null))
        )
        
        // First call fails with 401, second call succeeds
        coEvery { 
            api.getFeedsPaged(
                apiKey = any(),
                authorization = "Bearer mock_token",
                select = any(),
                order = any(),
                limit = any(),
                offset = any()
            ) 
        } throws unauthorizedException

        val mockRefreshResponse = RefreshTokenResponse(
            accessToken = "new_mock_token",
            tokenType = "bearer",
            expiresIn = 3600,
            refreshToken = "mock_refresh_token",
            user = RefreshTokenUser("id", "email")
        )
        coEvery { 
            usersApi.refreshToken(
                grantType = any(),
                refreshTokenRequest = any(),
                apiKey = any()
            ) 
        } returns mockRefreshResponse

        val mockFeedsResponse = listOf(
            PostResponseDto("1", "date", "content", "user", emptyList())
        )
        coEvery { 
            api.getFeedsPaged(
                apiKey = any(),
                authorization = "Bearer new_mock_token",
                select = any(),
                order = any(),
                limit = any(),
                offset = any()
            ) 
        } returns mockFeedsResponse

        val pagingState = PagingState<Int, PostWithMedia>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        // When
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        coVerify(exactly = 1) { 
            usersApi.refreshToken(
                grantType = any(),
                refreshTokenRequest = any(),
                apiKey = any()
            ) 
        }
        coVerify(exactly = 1) { sessionManager.updateAccessToken("new_mock_token") }
    }
}
