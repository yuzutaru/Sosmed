package com.yustar.dashboard.presentation.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.model.PostProfile
import com.yustar.dashboard.presentation.state.FeedsUiState
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class FeedsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPosts = listOf(
        Post(
            id = "1",
            createdAt = "2023-10-27T10:00:00Z",
            content = "Post 1 content",
            userId = "user1",
            postMedia = listOf(PostMedia("m1", "1", "url1", "image")),
            postProfile = PostProfile("John", "Doe")
        ),
        Post(
            id = "2",
            createdAt = "2023-10-27T11:00:00Z",
            content = "Post 2 content",
            userId = "user2",
            postMedia = listOf(PostMedia("m2", "2", "url2", "image")),
            postProfile = PostProfile("Jane", "Smith")
        )
    )

    @Test
    fun feedsContent_displaysPosts() {
        composeTestRule.setContent {
            SosmedTheme {
                val posts = flowOf(PagingData.from(mockPosts)).collectAsLazyPagingItems()
                FeedsContent(
                    innerPadding = PaddingValues(0.dp),
                    posts = posts,
                    uiState = FeedsUiState()
                )
            }
        }

        // Check if posts are displayed. PostWidget now uses "firstName lastName" from postProfile.
        composeTestRule.onAllNodesWithText("John Doe").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Jane Smith").onFirst().assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Post Media").onFirst().assertIsDisplayed()
    }

    @Test
    fun feedsContent_showsLoadingState_onRefresh() {
        composeTestRule.setContent {
            SosmedTheme {
                val posts = flowOf(
                    PagingData.empty<Post>(
                        sourceLoadStates = LoadStates(
                            refresh = LoadState.Loading,
                            prepend = LoadState.NotLoading(false),
                            append = LoadState.NotLoading(false)
                        )
                    )
                ).collectAsLazyPagingItems()
                FeedsContent(
                    innerPadding = PaddingValues(0.dp),
                    posts = posts,
                    uiState = FeedsUiState()
                )
            }
        }

        // Verify CircularProgressIndicator is shown using the test tag
        composeTestRule.onNodeWithTag("refresh_loading_indicator").assertIsDisplayed()
    }

    @Test
    fun feedsContent_showsLoadingState_onUiStateLoading() {
        composeTestRule.setContent {
            SosmedTheme {
                val posts = flowOf(
                    PagingData.empty<Post>()
                ).collectAsLazyPagingItems()
                FeedsContent(
                    innerPadding = PaddingValues(0.dp),
                    posts = posts,
                    uiState = FeedsUiState(isLoading = true)
                )
            }
        }

        // Verify CircularProgressIndicator is shown using the test tag
        composeTestRule.onNodeWithTag("refresh_loading_indicator").assertIsDisplayed()
    }

    @Test
    fun feedsContent_showsLoadingState_onAppend() {
        composeTestRule.setContent {
            SosmedTheme {
                val posts = flowOf(
                    PagingData.from(
                        mockPosts,
                        sourceLoadStates = LoadStates(
                            refresh = LoadState.NotLoading(false),
                            prepend = LoadState.NotLoading(false),
                            append = LoadState.Loading
                        )
                    )
                ).collectAsLazyPagingItems()
                FeedsContent(
                    innerPadding = PaddingValues(0.dp),
                    posts = posts,
                    uiState = FeedsUiState()
                )
            }
        }

        // Since the loading indicator is an item in LazyColumn, we need to scroll to it
        // if it's not currently on screen.
        composeTestRule.onNodeWithTag("feeds_list")
            .performScrollToIndex(mockPosts.size)

        // Verify CircularProgressIndicator is shown for append
        composeTestRule.onNodeWithTag("append_loading_indicator").assertIsDisplayed()
    }
}
