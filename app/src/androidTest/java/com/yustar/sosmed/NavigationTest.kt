package com.yustar.pokeapp_jetpackcompose

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import com.yustar.auth.R
import com.yustar.auth.domain.LoginResult
import com.yustar.auth.domain.LoginUserUseCase
import com.yustar.auth.domain.RegisterUserUseCase
import com.yustar.auth.presentation.viewmodel.LoginViewModel
import com.yustar.auth.presentation.viewmodel.RegisterViewModel
import com.yustar.sosmed.SosmedAppNavHost
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest

class NavigationTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val fakeLoginUserUseCase = FakeLoginUserUseCase()

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(module {
                factory<LoginUserUseCase> { fakeLoginUserUseCase }
                factory<RegisterUserUseCase> { mockk(relaxed = true) }
                viewModel { LoginViewModel(get()) }
                viewModel { RegisterViewModel(get()) }
            })
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun navHost_startDestinationIsLogin() {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            SosmedAppNavHost(navController = navController, startDestination = "login_route")
        }

        // Check that the login screen is displayed
        composeTestRule.onNodeWithText(context.getString(R.string.login_to_your_account)).assertIsDisplayed()
        
        // Verify current destination is "login" (the start destination inside "login_route")
        assertEquals("login", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun navHost_loginSuccess_navigatesToMenu() {
        fakeLoginUserUseCase.result = LoginResult.Success
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            SosmedAppNavHost(navController = navController, startDestination = "login_route")
        }

        // Input some text
        composeTestRule.onNodeWithText(context.getString(R.string.input_email)).performTextInput("test@example.com")
        composeTestRule.onNodeWithText(context.getString(R.string.input_password)).performTextInput("password")

        // Click login
        composeTestRule.onNodeWithText(context.getString(R.string.login)).performClick()

        // Verify current destination is "menu" (the start destination inside "menu_route")
        composeTestRule.waitForIdle()
        assertEquals("menu", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun navHost_clickRegister_navigatesToRegister() {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            SosmedAppNavHost(navController = navController, startDestination = "login_route")
        }

        // Click register
        composeTestRule.onNodeWithText(context.getString(R.string.register)).performClick()

        // Verify current destination is "register"
        composeTestRule.waitForIdle()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }
}
