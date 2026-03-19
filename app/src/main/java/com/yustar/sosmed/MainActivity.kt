package com.yustar.sosmed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.yustar.auth.authGraph
import com.yustar.core.session.SessionManager
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val sessionManager: SessionManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val loggedUser by sessionManager.loggedUser
                .collectAsState(initial = null)

            val startRoute = if (loggedUser != null) "menu_route" else "login_route"

            SosmedAppNavHost(
                navController = navController,
                startDestination = startRoute
            )
        }
    }
}

@Composable
fun SosmedAppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController)
        //menuGraph(navController)
    }
}