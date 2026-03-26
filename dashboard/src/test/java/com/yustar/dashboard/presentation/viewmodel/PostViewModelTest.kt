package com.yustar.dashboard.presentation.viewmodel

import android.net.Uri
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.usecase.GetLocalAlbumsUseCase
import com.yustar.dashboard.domain.usecase.GetLocalImagesUseCase
import com.yustar.dashboard.presentation.event.PostUiEvent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val getLocalImagesUseCase: GetLocalImagesUseCase = mockk()
    private val getLocalAlbumsUseCase: GetLocalAlbumsUseCase = mockk()
    private lateinit var viewModel: PostViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockUri = mockk<Uri>()
    private val localImages = listOf(
        LocalMedia(id = 1L, uri = mockUri, name = "image1.jpg", dateAdded = 1000L),
        LocalMedia(id = 2L, uri = mockUri, name = "image2.jpg", dateAdded = 2000L)
    )
    private val albums = listOf(
        AlbumItem(id = "1", name = "Album 1", count = "10", thumbnailUri = "uri1"),
        AlbumItem(id = "2", name = "Album 2", count = "20", thumbnailUri = "uri2")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { getLocalImagesUseCase(any()) } returns flowOf(localImages)
        every { getLocalAlbumsUseCase() } returns flowOf(albums)
        viewModel = PostViewModel(getLocalImagesUseCase, getLocalAlbumsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should load local images and albums`() = runTest {
        advanceUntilIdle()

        assertEquals(localImages, viewModel.uiState.value.localImages)
        assertEquals(albums, viewModel.uiState.value.albums)
        assertEquals(localImages.first(), viewModel.uiState.value.selectedImage)
    }

    @Test
    fun `setTabs should update uiState tabs`() = runTest {
        val tabs = listOf("Tab 1", "Tab 2")
        viewModel.setTabs(tabs)

        assertEquals(tabs, viewModel.uiState.value.tabs)
    }

    @Test
    fun `loadLocalImages should update localImages and selectedImage if null`() = runTest {
        // Initial load happens in init, selectedImage is set to localImages.first()
        advanceUntilIdle()
        assertEquals(localImages.first(), viewModel.uiState.value.selectedImage)

        val newImages = listOf(LocalMedia(id = 3L, uri = mockUri, name = "image3.jpg", dateAdded = 3000L))
        every { getLocalImagesUseCase(any()) } returns flowOf(newImages)

        viewModel.loadLocalImages("bucketId")
        advanceUntilIdle()

        assertEquals(newImages, viewModel.uiState.value.localImages)
        // selectedImage should still be the first one from init because it was not null
        assertEquals(localImages.first(), viewModel.uiState.value.selectedImage)
    }

    @Test
    fun `onEvent ShowAlbumSelection should update showAlbumSelection`() = runTest {
        viewModel.onEvent(PostUiEvent.ShowAlbumSelection(true))
        assertEquals(true, viewModel.uiState.value.showAlbumSelection)

        viewModel.onEvent(PostUiEvent.ShowAlbumSelection(false))
        assertEquals(false, viewModel.uiState.value.showAlbumSelection)
    }

    @Test
    fun `onEvent OnAlbumSelected should update selectedAlbum`() = runTest {
        val album = albums[1]
        viewModel.onEvent(PostUiEvent.OnAlbumSelected(album))
        assertEquals(album, viewModel.uiState.value.selectedAlbum)
    }

    @Test
    fun `onEvent OnImageSelected should update selectedImage`() = runTest {
        val image = localImages[1]
        viewModel.onEvent(PostUiEvent.OnImageSelected(image))
        assertEquals(image, viewModel.uiState.value.selectedImage)
    }

    @Test
    fun `onEvent OnTabSelected should update selectedTab`() = runTest {
        val tab = 1
        viewModel.onEvent(PostUiEvent.OnTabSelected(tab))
        assertEquals(tab, viewModel.uiState.value.selectedTab)
    }
}
