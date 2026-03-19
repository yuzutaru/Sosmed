package com.yustar.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.yustar.auth.presentation.screen.LoginScreen
import com.yustar.auth.presentation.screen.RegisterScreen

/**
 * Created by Yustar Pramudana on 08/03/26.
 */

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(route = "login_route", startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("menu_route") {
                        popUpTo("login_route") {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(onRegisterSuccess = { navController.navigateUp() })
        }
    }
}
