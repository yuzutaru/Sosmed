package com.yustar.dashboard.presentation.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia
import com.yustar.dashboard.domain.model.PostProfile
import com.yustar.dashboard.presentation.state.FeedsUiState
import com.yustar.dashboard.presentation.viewmodel.FeedsViewModel
import com.yustar.dashboard.presentation.widget.PostWidget
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

/**
 * Created by Yustar Pramudana on 21/03/26.
 */

@Composable
fun FeedsScreen(innerPadding: PaddingValues, viewModel: FeedsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val posts = viewModel.feeds.collectAsLazyPagingItems()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        if (uiState.error.isNotEmpty()) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    FeedsContent(innerPadding, posts, uiState)
}

@Composable
fun FeedsContent(
    innerPadding: PaddingValues,
    posts: LazyPagingItems<Post>,
    uiState: FeedsUiState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("feeds_list")
        ) {
            items(
                count = posts.itemCount,
                key = posts.itemKey { it.id }
            ) { index ->
                val post = posts[index]
                if (post != null) {
                    PostWidget(post = post)
                }
            }

            when (posts.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.testTag("append_loading_indicator"))
                        }
                    }
                }

                is LoadState.Error -> {
                    // Handle error
                }

                else -> {}
            }
        }

        if (posts.loadState.refresh is LoadState.Loading || uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("refresh_loading_indicator")
            )
        }
    }
}

private val samplePosts = listOf(
    Post(
        id = "1",
        createdAt = "2023-10-27T10:00:00Z",
        content = "Enjoying the sunset!",
        userId = "user_1",
        postMedia = listOf(
            PostMedia("m1", "1", "https://picsum.photos/seed/post1/800/800", "image")
        ),
        postProfile = PostProfile(firstName = "John", lastName = "Doe")
    ),
    Post(
        id = "2",
        createdAt = "2023-10-27T11:00:00Z",
        content = "Check out this view.",
        userId = "user_2",
        postMedia = listOf(
            PostMedia("m2", "2", "https://picsum.photos/seed/post2/800/800", "image")
        ),
        postProfile = PostProfile(firstName = "Jane", lastName = "Smith")
    )
)

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewFeedsScreen() {
    SosmedTheme {
        val posts = flowOf(PagingData.from(samplePosts)).collectAsLazyPagingItems()
        FeedsContent(
            innerPadding = PaddingValues(0.dp),
            posts = posts,
            uiState = FeedsUiState()
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewFeedsScreen() {
    SosmedTheme {
        val posts = flowOf(PagingData.from(samplePosts)).collectAsLazyPagingItems()
        FeedsContent(
            innerPadding = PaddingValues(0.dp),
            posts = posts,
            uiState = FeedsUiState()
        )
    }
}
