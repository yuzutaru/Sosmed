package com.yustar.dashboard.domain.usecase

import com.yustar.core.data.remote.model.Resource
import com.yustar.dashboard.data.remote.model.MediaDto
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.data.repository.FeedsRepository
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
    fun `invoke should call repository createPost and return success`() = runTest {
        // Given
        val caption = "test caption"
        val location = "test location"
        val media = listOf(PostMedia(id = "1", postId = "1", url = "url", mediaType = "image"))
        val mediaDto = listOf(MediaDto(media_url = "url", media_type = "image", position = 0))

        coEvery { repository.createPost(caption, location, mediaDto) } returns Unit

        // When
        val result = createPostUseCase(caption, location, media)

        // Then
        assertEquals(Resource.success(Unit), result)
        coVerify(exactly = 1) { repository.createPost(caption, location, mediaDto) }
    }

    @Test
    fun `invoke should return error when repository throws exception`() = runTest {
        // Given
        val caption = "test caption"
        val location = "test location"
        val media = listOf(PostMedia(id = "1", postId = "1", url = "url", mediaType = "image"))
        val mediaDto = listOf(MediaDto(media_url = "url", media_type = "image", position = 0))
        val errorMessage = "Error message"

        coEvery { repository.createPost(caption, location, mediaDto) } throws Exception(errorMessage)

        // When
        val result = createPostUseCase(caption, location, media)

        // Then
        assertEquals(Resource.error(null, errorMessage), result)
    }
}
