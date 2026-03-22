package com.yustar.dashboard.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yustar.dashboard.data.local.FeedsDatabase
import com.yustar.dashboard.data.local.model.PostWithMedia
import com.yustar.dashboard.data.remote.FeedsApi
import com.yustar.dashboard.data.remote.model.PostResponseDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class FeedsRemoteMediatorTest {

    private lateinit var database: FeedsDatabase
    private val api: FeedsApi = mockk()
    private lateinit var remoteMediator: FeedsRemoteMediator

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FeedsDatabase::class.java
        ).allowMainThreadQueries().build()
        
        remoteMediator = FeedsRemoteMediator(api, database)
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
}
