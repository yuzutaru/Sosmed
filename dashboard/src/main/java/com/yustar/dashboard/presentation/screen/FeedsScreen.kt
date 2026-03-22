package com.yustar.dashboard.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.presentation.viewmodel.FeedsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Created by Yustar Pramudana on 21/03/26.
 */

@Composable
fun FeedsScreen(innerPadding: PaddingValues, viewModel: FeedsViewModel = koinViewModel()) {
    FeedsContent(innerPadding)
}

@Composable
fun FeedsContent(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewFeedsContent() {
    SosmedTheme {
        FeedsContent(PaddingValues())
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewFeedsContent() {
    SosmedTheme {
        FeedsContent(PaddingValues())
    }
}