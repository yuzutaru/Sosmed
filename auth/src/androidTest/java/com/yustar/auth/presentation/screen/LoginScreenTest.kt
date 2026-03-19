package com.yustar.auth.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.yustar.auth.R
import com.yustar.auth.presentation.state.LoginUiState
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun loginContent_displaysAllElements() {
        composeTestRule.setContent {
            LoginContent(
                uiState = LoginUiState(),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLogin = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.login_to_your_account)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.email)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.password)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.login)).assertIsDisplayed()
    }

    @Test
    fun loginContent_showsError_whenErrorStateIsNotEmpty() {
        val errorMessage = "Invalid credentials"
        composeTestRule.setContent {
            LoginContent(
                uiState = LoginUiState(error = errorMessage),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLogin = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun loginContent_showsLoading_whenLoadingStateIsTrue() {
        composeTestRule.setContent {
            LoginContent(
                uiState = LoginUiState(isLoading = true),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLogin = {},
                onRegisterClick = {}
            )
        }

        // When loading, the "Login" text is not present as it's replaced by CircularProgressIndicator
        composeTestRule.onNodeWithText(context.getString(R.string.login)).assertDoesNotExist()
    }

    @Test
    fun loginContent_callsOnEmailChanged() {
        var email = ""
        composeTestRule.setContent {
            LoginContent(
                uiState = LoginUiState(),
                onEmailChanged = { email = it },
                onPasswordChanged = {},
                onLogin = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_email)).performTextInput("test@example.com")
        assert(email == "test@example.com")
    }

    @Test
    fun loginContent_callsOnPasswordChanged() {
        var password = ""
        composeTestRule.setContent {
            LoginContent(
                uiState = LoginUiState(),
                onEmailChanged = {},
                onPasswordChanged = { password = it },
                onLogin = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_password)).performTextInput("password123")
        assert(password == "password123")
    }

    @Test
    fun loginContent_callsOnLogin() {
        var loginClicked = false
        composeTestRule.setContent {
            LoginContent(
                uiState = LoginUiState(),
                onEmailChanged = {},
                onPasswordChanged = {},
                onLogin = { loginClicked = true },
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.login)).performClick()
        assert(loginClicked)
    }
}
