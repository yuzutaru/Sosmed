package com.yustar.dashboard.presentation.screen

import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.presentation.state.PostUiState
import com.yustar.dashboard.presentation.viewmodel.PostViewModel
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class PostScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockLocalMedia = listOf(
        LocalMedia(1L, Uri.parse("content://media/external/images/media/1"), "image1.jpg", 123456789L),
        LocalMedia(2L, Uri.parse("content://media/external/images/media/2"), "image2.jpg", 123456790L)
    )

    private val mockAlbum = AlbumItem("1", "Recents", "2", "content://media/external/images/media/1")
    private val mockViewModel: PostViewModel = mockk(relaxed = true)

    @Test
    fun postContent_displaysHeaderAndInitialState() {
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(),
                    onClose = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST", "STORY"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {}
                )
            }
        }

        composeTestRule.onNodeWithText("New post").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next").assertIsDisplayed()
        composeTestRule.onNodeWithText("Recents").assertIsDisplayed()
        composeTestRule.onNodeWithText("SELECT").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Camera").assertIsDisplayed()
    }

    @Test
    fun postContent_onImageSelected_callsCallback() {
        val onImageSelected: (LocalMedia) -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(localImages = mockLocalMedia),
                    onClose = {},
                    selectedImage = null,
                    localImages = mockLocalMedia,
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = onImageSelected,
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_image_item_0").performClick()
        verify { onImageSelected(mockLocalMedia[0]) }
        confirmVerified(onImageSelected)
    }

    @Test
    fun postContent_onTabSelected_callsCallback() {
        val onTabSelected: (Int) -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(),
                    onClose = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST", "STORY"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = onTabSelected,
                    onAlbumSelected = {},
                    onShowAlbumSelection = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_tab_1").performClick()
        verify { onTabSelected(1) }
        confirmVerified(onTabSelected)
    }

    @Test
    fun postContent_onClose_callsCallback() {
        val onClose: () -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(),
                    onClose = onClose,
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_close_button").performClick()
        verify { onClose() }
        confirmVerified(onClose)
    }

    @Test
    fun postContent_onShowAlbumSelection_callsCallback() {
        val onShowAlbumSelection: (Boolean) -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(),
                    onClose = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = onShowAlbumSelection
                )
            }
        }

        composeTestRule.onNodeWithTag("post_album_selector").performClick()
        verify { onShowAlbumSelection(true) }
        confirmVerified(onShowAlbumSelection)
    }

    @Test
    fun postContent_whenShowAlbumSelectionIsTrue_showsAlbumDialog() {
        // Stub the uiState flow to return a valid PostUiState
        every { mockViewModel.uiState } returns MutableStateFlow(PostUiState())

        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(showAlbumSelection = true),
                    onClose = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    viewModel = mockViewModel
                )
            }
        }

        // Verify that the album selection dialog content is displayed
        composeTestRule.onNodeWithText("Select album").assertIsDisplayed()
    }
}
