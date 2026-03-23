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
        ),
        postProfile = PostProfile("dagelan", "")
    )

    @Test
    fun postWidget_displaysDefaultValues() {
        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(post = mockPost)
            }
        }

        // Verify username (constructed as "firstName lastName")
        composeTestRule.onNodeWithText("dagelan ").assertIsDisplayed()

        // Verify initials are shown when avatarUrl is null ("dagelan" -> "D")
        composeTestRule.onNodeWithText("D").assertIsDisplayed()
        
        // Verify default counts
        composeTestRule.onNodeWithText("27.7K").assertIsDisplayed()
        composeTestRule.onNodeWithText("317").assertIsDisplayed()
        composeTestRule.onNodeWithText("310").assertIsDisplayed()
        
        // Verify default liked by
        composeTestRule.onNodeWithText("Liked by febrian_joss and others").assertIsDisplayed()
        
        // Verify key UI components via content descriptions
        composeTestRule.onNodeWithContentDescription("Post Media").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Mute").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Share").assertIsDisplayed()
    }

    @Test
    fun postWidget_displaysCustomValues() {
        val customUsername = "test_user"
        val customLikes = "100"
        val customComments = "50"
        val customReposts = "10"
        val customLikedBy = "another_user"
        val customAvatarUrl = "https://example.com/avatar.jpg"

        composeTestRule.setContent {
            SosmedTheme {
                PostWidget(
                    post = mockPost,
                    username = customUsername,
                    avatarUrl = customAvatarUrl,
                    likeCount = customLikes,
                    commentCount = customComments,
                    repostCount = customReposts,
                    likedBy = customLikedBy
                )
            }
        }

        // Verify custom values are rendered
        composeTestRule.onNodeWithText(customUsername).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Avatar").assertIsDisplayed()
        composeTestRule.onNodeWithText(customLikes).assertIsDisplayed()
        composeTestRule.onNodeWithText(customComments).assertIsDisplayed()
        composeTestRule.onNodeWithText(customReposts).assertIsDisplayed()
        composeTestRule.onNodeWithText("Liked by $customLikedBy and others").assertIsDisplayed()
    }
}
