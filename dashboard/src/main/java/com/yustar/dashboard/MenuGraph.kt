package com.yustar.dashboard

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.yustar.dashboard.presentation.screen.DashboardScreen
import com.yustar.dashboard.presentation.screen.PostEditScreen
import com.yustar.dashboard.presentation.screen.PostScreen

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
        composable("post") {
            // PostScreen
            PostScreen(
                onClose = { navController.popBackStack() },
                onNext = { uri ->
                    navController.navigate("post_edit/${Uri.encode(uri.toString())}")
                }
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
            PostEditScreen(
                mediaUri = mediaUri,
                onClose = { navController.popBackStack() },
                onNext = { /* Navigate to final post screen */ }
            )
        }
    }
}
