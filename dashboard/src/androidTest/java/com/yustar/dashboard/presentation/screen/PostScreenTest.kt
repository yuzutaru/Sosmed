package com.yustar.dashboard.presentation.screen

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.LocalMedia
import com.yustar.dashboard.domain.model.MediaType
import com.yustar.dashboard.presentation.state.PostUiState
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class PostScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val mockLocalMedia = listOf(
        LocalMedia(1L, Uri.parse("content://media/external/images/media/1"), "image1.jpg", 123456789L),
        LocalMedia(2L, Uri.parse("content://media/external/images/media/2"), "image2.jpg", 123456790L)
    )

    private val mockAlbum = AlbumItem("1", "Recents", "2", "content://media/external/images/media/1")

    @Test
    fun minimalTest() {
        composeTestRule.setContent {
            Text("Hello World")
        }
        composeTestRule.onNodeWithText("Hello World").assertIsDisplayed()
    }

    @Test
    fun postContent_displaysHeaderAndInitialState() {
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(),
                    onClose = {},
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST", "STORY"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
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
                    onNext = {},
                    selectedImage = null,
                    localImages = mockLocalMedia,
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = onImageSelected,
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_image_item_0").assertIsDisplayed().performClick()
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
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST", "STORY"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = onTabSelected,
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_tab_1").assertIsDisplayed().performClick()
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
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_close_button").assertIsDisplayed().performClick()
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
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = onShowAlbumSelection,
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_album_selector").assertIsDisplayed().performClick()
        verify { onShowAlbumSelection(true) }
        confirmVerified(onShowAlbumSelection)
    }

    @Test
    fun postContent_whenShowAlbumSelectionIsTrue_showsAlbumDialog() {
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(showAlbumSelection = true),
                    onClose = {},
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        // Verify that the album selection dialog content is displayed
        composeTestRule.onNodeWithText("Select album").assertIsDisplayed()
    }

    @Test
    fun postContent_onNext_callsCallback() {
        val onNext: () -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(selectedImage = mockLocalMedia[0]),
                    onClose = {},
                    onNext = onNext,
                    selectedImage = mockLocalMedia[0],
                    localImages = mockLocalMedia,
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_next_button").assertIsDisplayed().performClick()
        verify { onNext() }
        confirmVerified(onNext)
    }

    @Test
    fun postContent_nextButtonDisabled_whenNoImageSelected() {
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(selectedImage = null),
                    onClose = {},
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = null,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("post_next_button").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun postContent_onCategoryClicked_callsCallback() {
        val onCategoryClicked: (MediaType) -> Unit = mockk(relaxed = true)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(showAlbumSelection = true),
                    onClose = {},
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = {},
                    onShowAlbumSelection = {},
                    onCategoryClicked = onCategoryClicked
                )
            }
        }

        composeTestRule.onNodeWithTag("Category_Photos").assertIsDisplayed().performClick()
        verify { onCategoryClicked(MediaType.PHOTOS) }
        confirmVerified(onCategoryClicked)
    }

    @Test
    fun postContent_onAlbumSelected_callsCallback() {
        val onAlbumSelected: (AlbumItem) -> Unit = mockk(relaxed = true)
        val albums = listOf(mockAlbum)
        composeTestRule.setContent {
            SosmedTheme {
                PostContent(
                    uiState = PostUiState(showAlbumSelection = true, albums = albums),
                    onClose = {},
                    onNext = {},
                    selectedImage = null,
                    localImages = emptyList(),
                    selectedAlbum = mockAlbum,
                    tabs = listOf("POST"),
                    selectedTab = 0,
                    onImageSelected = {},
                    onTabSelected = {},
                    onAlbumSelected = onAlbumSelected,
                    onShowAlbumSelection = {},
                    onCategoryClicked = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("AlbumItem_${mockAlbum.name}").assertIsDisplayed().performClick()
        verify { onAlbumSelected(mockAlbum) }
        confirmVerified(onAlbumSelected)
    }
}
