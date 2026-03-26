package com.yustar.dashboard.domain.usecase

import android.net.Uri
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.model.MediaType
import com.yustar.dashboard.domain.repository.FeedsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetLocalImagesUseCaseTest {

    private lateinit var repository: FeedsRepository
    private lateinit var getLocalImagesUseCase: GetLocalImagesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        getLocalImagesUseCase = GetLocalImagesUseCase(repository)
    }

    @Test
    fun `invoke should return flow of local media from repository with default parameters`() = runTest {
        // Given
        val mockUri = mockk<Uri>()
        val localMediaList = listOf(
            LocalMedia(id = 1L, uri = mockUri, name = "image1.jpg", dateAdded = 1000L),
            LocalMedia(id = 2L, uri = mockUri, name = "image2.jpg", dateAdded = 2000L)
        )
        val expectedFlow = flowOf(localMediaList)

        every { repository.getLocalImages(null, MediaType.PHOTOS) } returns expectedFlow

        // When
        val result = getLocalImagesUseCase().first()

        // Then
        assertEquals(localMediaList, result)
    }

    @Test
    fun `invoke should return flow of local media from repository with custom parameters`() = runTest {
        // Given
        val bucketId = "bucket123"
        val type = MediaType.VIDEOS
        val mockUri = mockk<Uri>()
        val localMediaList = listOf(
            LocalMedia(id = 1L, uri = mockUri, name = "video1.mp4", dateAdded = 1000L)
        )
        val expectedFlow = flowOf(localMediaList)

        every { repository.getLocalImages(bucketId, type) } returns expectedFlow

        // When
        val result = getLocalImagesUseCase(bucketId, type).first()

        // Then
        assertEquals(localMediaList, result)
    }
}
