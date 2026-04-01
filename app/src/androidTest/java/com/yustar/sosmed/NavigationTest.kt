package com.yustar.sosmed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
import com.yustar.dashboard.di.SupabaseModule
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import io.github.jan.supabase.SupabaseClient
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@UninstallModules(SupabaseModule::class)
@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @BindValue
    val loginUserUseCase: LoginUserUseCase = mockk(relaxed = true)

    @BindValue
    val registerUserUseCase: RegisterUserUseCase = mockk(relaxed = true)

    // Mocking SupabaseClient to prevent ExceptionInInitializerError during initialization
    @BindValue
    val supabaseClient: SupabaseClient = mockk(relaxed = true)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun navHost_startDestinationIsLogin() {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(context)
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
        coEvery { loginUserUseCase.invoke(any(), any()) } returns LoginResult.Success

        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(context)
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
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            SosmedAppNavHost(navController = navController, startDestination = "login_route")
        }

        // Click register
        composeTestRule.onNodeWithText(context.getString(R.string.register)).performClick()

        // Verify current destination is "register"
        composeTestRule.waitForIdle()
        assertEquals("register", navController.currentBackStackEntry?.destination?.route)
    }

    @Test
    fun navHost_fromMenu_clickAdd_navigatesToPost() {
        lateinit var navController: TestNavHostController
        composeTestRule.setContent {
            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            SosmedAppNavHost(navController = navController, startDestination = "menu_route")
        }

        // Click Add button (Create) in Dashboard
        composeTestRule.onNodeWithContentDescription("Add").performClick()

        // Verify current destination is "post"
        composeTestRule.waitForIdle()
        assertEquals("post", navController.currentBackStackEntry?.destination?.route)
    }
}
