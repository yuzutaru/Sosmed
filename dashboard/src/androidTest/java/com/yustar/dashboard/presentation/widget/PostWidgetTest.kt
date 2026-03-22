package com.yustar.dashboard.presentation.widget

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import org.junit.Rule
import org.junit.Test

class PostWidgetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPost = Post(
        id = "1",
        createdAt = "2023-10-27T10:00:00Z",
        content = "This is a test post",
        userId = "user1",
        postMedia = listOf(
            PostMedia(
                id = "m1",
                postId = "1",
                url = "https://example.com/image.jpg",
                mediaType = "image"
            )
        )
    )

    @Test
    fun postWidget_displaysDefaultValues() {
        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(post = mockPost)
            }
        }

        // Verify default username
        composeTestRule.onNodeWithText("dagelan").assertIsDisplayed()
        
        // Verify default music name
        composeTestRule.onNodeWithText("Edith Whiskers • Home").assertIsDisplayed()
        
        // Verify default counts
        composeTestRule.onNodeWithText("27.7K").assertIsDisplayed()
        composeTestRule.onNodeWithText("317").assertIsDisplayed()
        composeTestRule.onNodeWithText("310").assertIsDisplayed()
        
        // Verify default liked by
        composeTestRule.onNodeWithText("Liked by febrian_joss and others").assertIsDisplayed()
        
        // Verify key UI components via content descriptions
        composeTestRule.onNodeWithContentDescription("Avatar").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Verified").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Post Media").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Share").assertIsDisplayed()
    }

    @Test
    fun postWidget_displaysCustomValues() {
        val customUsername = "test_user"
        val customMusic = "Custom Music Name"
        val customLikes = "100"
        val customComments = "50"
        val customReposts = "10"
        val customLikedBy = "another_user"

        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(
                    post = mockPost,
                    username = customUsername,
                    musicName = customMusic,
                    likeCount = customLikes,
                    commentCount = customComments,
                    repostCount = customReposts,
                    likedBy = customLikedBy
                )
            }
        }

        // Verify custom values are rendered
        composeTestRule.onNodeWithText(customUsername).assertIsDisplayed()
        composeTestRule.onNodeWithText(customMusic).assertIsDisplayed()
        composeTestRule.onNodeWithText(customLikes).assertIsDisplayed()
        composeTestRule.onNodeWithText(customComments).assertIsDisplayed()
        composeTestRule.onNodeWithText(customReposts).assertIsDisplayed()
        composeTestRule.onNodeWithText("Liked by $customLikedBy and others").assertIsDisplayed()
    }
}
