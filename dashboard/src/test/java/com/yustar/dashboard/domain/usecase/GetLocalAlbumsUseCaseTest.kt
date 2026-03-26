package com.yustar.dashboard.domain.usecase

import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.repository.FeedsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetLocalAlbumsUseCaseTest {

    private lateinit var repository: FeedsRepository
    private lateinit var getLocalAlbumsUseCase: GetLocalAlbumsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getLocalAlbumsUseCase = GetLocalAlbumsUseCase(repository)
    }

    @Test
    fun `invoke should return flow of albums from repository`() = runTest {
        // Given
        val albumList = listOf(
            AlbumItem(id = "1", name = "Album 1", count = "10", thumbnailUri = "uri1"),
            AlbumItem(id = "2", name = "Album 2", count = "20", thumbnailUri = "uri2")
        )
        val expectedFlow = flowOf(albumList)

        every { repository.getLocalAlbums() } returns expectedFlow

        // When
        val result = getLocalAlbumsUseCase().first()

        // Then
        assertEquals(albumList, result)
    }
}
