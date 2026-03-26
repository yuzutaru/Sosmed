package com.yustar.dashboard.presentation.widget

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.R
import com.yustar.dashboard.domain.model.AlbumItem
import com.yustar.dashboard.domain.model.MediaType
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class SelectAlbumBottomSheetDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun selectAlbumBottomSheetDialogContent_displaysAllStaticElements() {
        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = {},
                    onAlbumSelected = {},
                    onCategoryClicked = {},
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.select_album)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).assertIsDisplayed()
        composeTestRule.onNodeWithTag("Category_${context.getString(R.string.recents)}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Category_${context.getString(R.string.photos)}").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Category_${context.getString(R.string.videos)}").assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.albums)).assertIsDisplayed()
    }

    @Test
    fun selectAlbumBottomSheetDialogContent_displaysAlbums() {
        val albums = listOf(
            AlbumItem("1", "Vacation", "10", null),
            AlbumItem("2", "Food", "5", null)
        )

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = {},
                    onAlbumSelected = {},
                    onCategoryClicked = {},
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = albums
                )
            }
        }

        composeTestRule.onNodeWithTag("AlbumItem_Vacation").assertIsDisplayed()
        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AlbumItem_Food").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun selectAlbumBottomSheetDialogContent_displaysVideoAlbumWithDuration() {
        val albums = listOf(
            AlbumItem("1", "Videos", "10", null, isVideo = true, duration = "05:30")
        )

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = {},
                    onAlbumSelected = {},
                    onCategoryClicked = {},
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = albums
                )
            }
        }

        composeTestRule.onNodeWithTag("AlbumItem_Videos").assertIsDisplayed()
        composeTestRule.onNodeWithText("05:30").assertIsDisplayed()
    }

    @Test
    fun clickingCancel_callsOnDismissRequest() {
        val onDismissRequest: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = onDismissRequest,
                    onAlbumSelected = {},
                    onCategoryClicked = {},
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).performClick()

        verify { onDismissRequest() }
    }

    @Test
    fun clickingRecentsCategory_callsOnCategoryClicked() {
        val onCategoryClicked: (MediaType) -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = {},
                    onAlbumSelected = {},
                    onCategoryClicked = onCategoryClicked,
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithTag("Category_${context.getString(R.string.recents)}").performClick()

        verify { onCategoryClicked(MediaType.RECENTS) }
    }

    @Test
    fun clickingPhotosCategory_callsOnCategoryClicked() {
        val onCategoryClicked: (MediaType) -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = {},
                    onAlbumSelected = {},
                    onCategoryClicked = onCategoryClicked,
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithTag("Category_${context.getString(R.string.photos)}").performClick()

        verify { onCategoryClicked(MediaType.PHOTOS) }
    }

    @Test
    fun clickingVideosCategory_callsOnCategoryClicked() {
        val onCategoryClicked: (MediaType) -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = {},
                    onAlbumSelected = {},
                    onCategoryClicked = onCategoryClicked,
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithTag("Category_${context.getString(R.string.videos)}").performClick()

        verify { onCategoryClicked(MediaType.VIDEOS) }
    }

    @Test
    fun clickingAlbumItem_callsOnAlbumSelectedAndDismiss() {
        val album = AlbumItem("1", "Vacation", "10", null)
        val onAlbumSelected: (AlbumItem) -> Unit = mockk(relaxed = true)
        val onDismissRequest: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = onDismissRequest,
                    onAlbumSelected = onAlbumSelected,
                    onCategoryClicked = {},
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                    albums = listOf(album)
                )
            }
        }

        composeTestRule.onNodeWithTag("AlbumItem_Vacation").performClick()

        verify {
            onAlbumSelected(album)
            onDismissRequest()
        }
    }
}
