package com.yustar.dashboard.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.R
import com.yustar.dashboard.presentation.event.PostUiEvent
import com.yustar.dashboard.presentation.state.PostUiState
import com.yustar.dashboard.presentation.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetCaptionScreen(
    viewModel: PostViewModel,
    onEvent: (PostUiEvent) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SetCaptionContent(uiState, onEvent, onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetCaptionContent(
    uiState: PostUiState,
    onEvent: (PostUiEvent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.new_post),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { onEvent(PostUiEvent.OnShareClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C53E8))
            ) {
                Text(
                    text = stringResource(R.string.share),
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Image and Caption Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = uiState.selectedAlbum,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                TextField(
                    value = uiState.caption,
                    onValueChange = { onEvent(PostUiEvent.OnCaptionChanged(it)) },
                    placeholder = { Text(stringResource(R.string.add_a_caption), color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // Poll and Prompt Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                InputChip(
                    selected = false,
                    onClick = { /* TODO */ },
                    label = { Text(stringResource(R.string.poll)) },
                    leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                InputChip(
                    selected = false,
                    onClick = { /* TODO */ },
                    label = { Text(stringResource(R.string.prompt)) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))

            // Menu Items
            CaptionMenuItem(
                icon = Icons.Outlined.MusicNote,
                label = stringResource(R.string.add_audio),
                onClick = { /* TODO */ }
            )

            // Music suggestion chips (Optional placeholder based on image)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(listOf("Anuar & Ellina · Suasana Di Hari Raya", "Mothership")) { music ->
                    SuggestionChip(
                        onClick = { /* TODO */ },
                        label = { Text(music, fontSize = 12.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            CaptionMenuItem(
                icon = Icons.Outlined.People,
                label = stringResource(R.string.tag_people),
                onClick = { /* TODO */ }
            )

            CaptionMenuItem(
                icon = Icons.Outlined.LocationOn,
                label = stringResource(R.string.add_location),
                onClick = { /* TODO */ }
            )

            // Location suggestions
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(listOf("Medan, Indonesia", "Perumahan Citra Graha", "Medan T...")) { location ->
                    SuggestionChip(
                        onClick = { /* TODO */ },
                        label = { Text(location, fontSize = 12.sp) },
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CaptionMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 16.sp)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewSetCaptionContentPreview() {
    SosmedTheme {
        SetCaptionContent(
            uiState = PostUiState(),
            onEvent = {},
            onBack = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewSetCaptionContentPreview() {
    SosmedTheme {
        SetCaptionContent(
            uiState = PostUiState(),
            onEvent = {},
            onBack = {}
        )
    }
}