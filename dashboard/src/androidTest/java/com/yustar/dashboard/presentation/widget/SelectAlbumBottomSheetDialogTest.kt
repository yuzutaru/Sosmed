package com.yustar.dashboard.presentation.widget

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.R
import com.yustar.dashboard.domain.model.AlbumItem
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
                    sheetState = rememberModalBottomSheetState(),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.select_album)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.recents)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.photos)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.videos)).assertIsDisplayed()
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
                    sheetState = rememberModalBottomSheetState(),
                    albums = albums
                )
            }
        }

        composeTestRule.onNodeWithText("Vacation").assertIsDisplayed()
        composeTestRule.onNodeWithText("10").assertIsDisplayed()
        composeTestRule.onNodeWithText("Food").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun clickingCancel_callsOnDismissRequest() {
        val onDismissRequest: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = onDismissRequest,
                    onAlbumSelected = {},
                    sheetState = rememberModalBottomSheetState(),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.cancel)).performClick()

        verify { onDismissRequest() }
    }

    @Test
    fun clickingRecentsCategory_callsOnAlbumSelectedAndDismiss() {
        val onAlbumSelected: (AlbumItem) -> Unit = mockk(relaxed = true)
        val onDismissRequest: () -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                SelectAlbumBottomSheetDialogContent(
                    onDismissRequest = onDismissRequest,
                    onAlbumSelected = onAlbumSelected,
                    sheetState = rememberModalBottomSheetState(),
                    albums = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.recents)).performClick()

        verify { 
            onAlbumSelected(match { it.id == "all" && it.name == context.getString(R.string.recents) })
            onDismissRequest()
        }
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
                    sheetState = rememberModalBottomSheetState(),
                    albums = listOf(album)
                )
            }
        }

        composeTestRule.onNodeWithText("Vacation").performClick()

        verify { 
            onAlbumSelected(album)
            onDismissRequest()
        }
    }
}
