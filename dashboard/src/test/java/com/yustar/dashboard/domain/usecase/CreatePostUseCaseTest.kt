package com.yustar.dashboard.domain.usecase

import com.yustar.core.data.remote.model.Resource
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.repository.FeedsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreatePostUseCaseTest {

    private lateinit var repository: FeedsRepository
    private lateinit var createPostUseCase: CreatePostUseCase

    @Before
    fun setUp() {
        repository = mockk()
        createPostUseCase = CreatePostUseCase(repository)
    }

    @Test
    fun `invoke should call repository createPost and return its result`() = runTest {
        // Given
        val caption = "test caption"
        val location = "test location"
        val media = listOf(PostMedia(id = "1", postId = "1", url = "url", mediaType = "image"))
        val expectedResult = Resource.success(Unit)

        coEvery { repository.createPost(caption, location, media) } returns expectedResult

        // When
        val result = createPostUseCase(caption, location, media)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.createPost(caption, location, media) }
    }
}
