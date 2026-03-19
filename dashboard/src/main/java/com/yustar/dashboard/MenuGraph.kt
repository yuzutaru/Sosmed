package com.yustar.dashboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.yustar.dashboard.presentation.screen.DashboardScreen

/**
 * Created by Yustar Pramudana on 08/03/26.
 */

fun NavGraphBuilder.menuGraph(navController: NavHostController) {
    navigation(route = "menu_route", startDestination = "menu" ) {
        composable("menu") {
            //Main Menu
            DashboardScreen()
        }
        composable("pokemon_detail/{pokemonName}") { backStackEntry ->
            val pokemonName = backStackEntry.arguments?.getString("pokemonName") ?: ""
            /*DetailScreen(
                pokemonName = pokemonName,
                onBackClick = { navController.popBackStack() }
            )*/
        }
    }
}
