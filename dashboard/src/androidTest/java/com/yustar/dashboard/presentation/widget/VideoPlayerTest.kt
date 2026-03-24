package com.yustar.dashboard.presentation.widget

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.yustar.core.ui.theme.SosmedTheme
import org.junit.Rule
import org.junit.Test

class VideoPlayerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun videoPlayer_isDisplayed() {
        val testUrl = "https://www.pexels.com/download/video/7856705/"
        
        composeTestRule.setContent {
            SosmedTheme {
                VideoPlayer(
                    url = testUrl,
                    modifier = Modifier.testTag("video_player_tag")
                )
            }
        }

        // Verify the VideoPlayer (AndroidView) is displayed
        composeTestRule.onNodeWithTag("video_player_tag").assertIsDisplayed()
    }
}
