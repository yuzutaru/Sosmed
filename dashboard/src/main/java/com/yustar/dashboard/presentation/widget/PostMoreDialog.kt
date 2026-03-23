package com.yustar.dashboard.presentation.widget

import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.yustar.core.ui.theme.SosmedTheme

/**
 * Created by Yustar Pramudana on 23/03/26.
 */

@Composable
fun PostMoreDialog(showMenu: Boolean, onDismissRequest: (Boolean) -> Unit) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { onDismissRequest(false) }
    ) {
        DropdownMenuItem(
            text = { Text("Why you're seeing this") },
            onClick = { onDismissRequest(false) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = { Text("Not interested") },
            onClick = { onDismissRequest(false) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.HighlightOff,
                    contentDescription = null
                )
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = "Report",
                    color = MaterialTheme.colorScheme.error
                )
            },
            onClick = { onDismissRequest(false) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Feedback,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePostMoreDialogPreview() {
    SosmedTheme {
        PostMoreDialog(false, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewPostMoreDialogScreen() {
    SosmedTheme {
        PostMoreDialog(false, {})
    }
}