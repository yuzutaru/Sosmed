package com.yustar.dashboard.presentation.widget

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.model.PostProfile
import org.junit.Rule
import org.junit.Test

class PostWidgetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPost = Post(
        id = "1",
        createdAt = "2 hours ago",
        content = "This is a test post content",
        userId = "user1",
        postMedia = listOf(
            PostMedia(
                id = "m1",
                postId = "1",
                url = "https://example.com/image.jpg",
                mediaType = "image"
            )
        ),
        postProfile = PostProfile("John", "Doe")
    )

    @Test
    fun postWidget_displaysPostData() {
        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(post = mockPost)
            }
        }

        // Verify username (constructed as "firstName lastName" by default)
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()

        // Verify initials (JD)
        composeTestRule.onNodeWithText("JD").assertIsDisplayed()
        
        // Verify content and timestamp
        composeTestRule.onNodeWithText("This is a test post content").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 hours ago").assertIsDisplayed()
        
        // Verify default counts
        composeTestRule.onNodeWithText("27.7K").assertIsDisplayed()
        composeTestRule.onNodeWithText("317").assertIsDisplayed()
        
        // Verify media
        composeTestRule.onNodeWithContentDescription("Post Media").assertIsDisplayed()
    }

    @Test
    fun postWidget_displaysMultipleMediaIndicator() {
        val multiMediaPost = mockPost.copy(
            postMedia = listOf(
                PostMedia("1", "1", "url1", "image"),
                PostMedia("2", "1", "url2", "image")
            )
        )

        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(post = multiMediaPost)
            }
        }

        // Verify page indicator for multiple media
        composeTestRule.onNodeWithText("1/2").assertIsDisplayed()
    }

    @Test
    fun postWidget_displaysVideoWithMuteIcon() {
        val videoPost = mockPost.copy(
            postMedia = listOf(
                PostMedia("1", "1", "url1", "video")
            )
        )

        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(post = videoPost)
            }
        }

        // Verify mute icon is shown for video
        composeTestRule.onNodeWithContentDescription("Mute").assertIsDisplayed()
    }

    @Test
    fun postWidget_displaysCustomValues() {
        val customUsername = "test_user"
        val customLikes = "100"
        val customLikedBy = "another_user"

        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(
                    post = mockPost,
                    username = customUsername,
                    likeCount = customLikes,
                    likedBy = customLikedBy
                )
            }
        }

        // Verify custom values are rendered
        composeTestRule.onNodeWithText(customUsername).assertIsDisplayed()
        composeTestRule.onNodeWithText(customLikes).assertIsDisplayed()
        composeTestRule.onNodeWithText("Liked by $customLikedBy and others").assertIsDisplayed()
    }
}
