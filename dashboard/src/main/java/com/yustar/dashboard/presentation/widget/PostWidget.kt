package com.yustar.dashboard.presentation.widget

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.domain.model.Post
import com.yustar.dashboard.domain.model.PostMedia

@Composable
fun PostWidget(
    post: Post,
    modifier: Modifier = Modifier,
    username: String = "dagelan", // Mocking since not in Post model yet
    avatarUrl: String? = null,
    musicName: String = "Edith Whiskers • Home",
    likeCount: String = "27.7K",
    commentCount: String = "317",
    repostCount: String = "310",
    likedBy: String = "febrian_joss"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        // Header
        PostHeader(
            username = username,
            avatarUrl = avatarUrl,
            musicName = musicName
        )

        // Media
        PostMedia(url = post.postMedia.firstOrNull()?.url)

        // Action Buttons
        PostActions(
            likeCount = likeCount,
            commentCount = commentCount,
            repostCount = repostCount
        )

        // Liked by section
        PostLikedBy(likedBy = likedBy)
    }
}

@Composable
private fun PostHeader(
    username: String,
    avatarUrl: String?,
    musicName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified",
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF1DA1F2)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Music",
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = musicName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More"
            )
        }
    }
}

@Composable
private fun PostMedia(url: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.LightGray)
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Post Media",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Mute icon as seen in the screenshot
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.VolumeOff,
            contentDescription = "Mute",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(20.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .padding(4.dp),
            tint = Color.White
        )
    }
}

@Composable
private fun PostActions(
    likeCount: String,
    commentCount: String,
    repostCount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        ActionButton(icon = Icons.Outlined.FavoriteBorder, text = likeCount)
        ActionButton(icon = Icons.Outlined.ChatBubbleOutline, text = commentCount)
        ActionButton(icon = Icons.Outlined.Repeat, text = repostCount)
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = "Share",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun PostLikedBy(likedBy: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Small overlapping avatars
        Box(modifier = Modifier.size(24.dp)) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
                    .align(Alignment.CenterStart)
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterEnd)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Liked by $likedBy and others",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 13.sp
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePostWidgetPreview() {
    SosmedTheme {
        PostWidget(
            post = Post(
                id = "1",
                createdAt = "",
                content = "Sample content",
                userId = "1",
                postMedia = listOf(PostMedia("1", "1", "https://picsum.photos/400", "image"))
            )
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewPostWidgetScreen() {
    SosmedTheme {
        PostWidget(
            post = Post(
                id = "1",
                createdAt = "",
                content = "Sample content",
                userId = "1",
                postMedia = listOf(PostMedia("1", "1", "https://picsum.photos/400", "image"))
            )
        )
    }
}