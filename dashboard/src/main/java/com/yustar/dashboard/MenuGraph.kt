package com.yustar.dashboard

import android.net.Uri
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.yustar.dashboard.presentation.screen.DashboardScreen
import com.yustar.dashboard.presentation.screen.PostEditScreen
import com.yustar.dashboard.presentation.screen.PostScreen
import com.yustar.dashboard.presentation.screen.SetCaptionScreen
import com.yustar.dashboard.presentation.viewmodel.PostViewModel

/**
 * Created by Yustar Pramudana on 08/03/26.
 */

fun NavGraphBuilder.menuGraph(navController: NavHostController) {
    navigation(route = "menu_route", startDestination = "menu" ) {
        composable("menu") {
            // Main Menu
            DashboardScreen(
                onAddClick = { navController.navigate("post") }
            )
        }
        composable("post") { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("menu_route")
            }
            val sharedViewModel: PostViewModel = hiltViewModel(parentEntry)

            // PostScreen
            PostScreen(
                onClose = { navController.popBackStack() },
                onNext = {
                    navController.navigate("post_caption")
                },
                viewModel = sharedViewModel
            )
        }
        composable(
            route = "post_edit/{mediaUri}",
            arguments = listOf(
                navArgument("mediaUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mediaUriString = backStackEntry.arguments?.getString("mediaUri")
            val mediaUri = Uri.parse(Uri.decode(mediaUriString))

            // Post Edit Screen
            PostEditScreen(
                mediaUri = mediaUri,
                onClose = { navController.popBackStack() },
                onNext = { /* Navigate to final post screen */ }
            )
        }
        composable(
            route = "post_caption"
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry("menu_route")
            }
            val sharedViewModel: PostViewModel = hiltViewModel(parentEntry)

            // Post Set Caption Screen
            SetCaptionScreen(
                viewModel = sharedViewModel, onEvent = {},
                onBack = { navController.popBackStack() }
            )
        }
    }
}
