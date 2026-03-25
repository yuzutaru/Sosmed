package com.yustar.dashboard.presentation.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.dashboard.presentation.widget.BottomNavItem

/**
 * Created by Yustar Pramudana on 19/03/26.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onAddClick: () -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Create,
        BottomNavItem.Reels,
        BottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            if (selectedItem == 0) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Sosmed",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onAddClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle Notifications */ }) {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Notifications"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            if (item is BottomNavItem.Create) {
                                onAddClick()
                            } else {
                                selectedItem = index
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItem == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        label = null,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedItem) {
            0 -> FeedsScreen(innerPadding)
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[selectedItem].label,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewDashboardScreen() {
    SosmedTheme {
        DashboardScreen()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewDashboardScreen() {
    SosmedTheme {
        DashboardScreen()
    }
}
