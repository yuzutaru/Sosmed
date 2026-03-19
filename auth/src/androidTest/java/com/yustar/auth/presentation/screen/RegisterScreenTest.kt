package com.yustar.auth.presentation.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.yustar.auth.R
import com.yustar.auth.presentation.state.RegisterUiState
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun registerContent_displaysAllElements() {
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.create_new_account)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.first_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.last_name)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.address)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.phone_number)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.email_or_username)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.password)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.register)).assertIsDisplayed()
    }

    @Test
    fun registerContent_callsOnFirstNameChanged() {
        var firstName = ""
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = { firstName = it },
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_first_name)).performTextInput("John")
        assert(firstName == "John")
    }

    @Test
    fun registerContent_callsOnLastNameChanged() {
        var lastName = ""
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = { lastName = it },
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_last_name)).performTextInput("Doe")
        assert(lastName == "Doe")
    }

    @Test
    fun registerContent_callsOnAddressChanged() {
        var address = ""
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = { address = it },
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_address)).performTextInput("123 Street")
        assert(address == "123 Street")
    }

    @Test
    fun registerContent_callsOnPhoneNumberChanged() {
        var phoneNumber = ""
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = { phoneNumber = it },
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_phone_number)).performTextInput("123456789")
        assert(phoneNumber == "123456789")
    }

    @Test
    fun registerContent_callsOnEmailChanged() {
        var email = ""
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = { email = it },
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_email)).performTextInput("test@example.com")
        assert(email == "test@example.com")
    }

    @Test
    fun registerContent_callsOnPasswordChanged() {
        var password = ""
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = { password = it },
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.input_password)).performTextInput("password123")
        assert(password == "password123")
    }

    @Test
    fun registerContent_callsOnRegisterClick() {
        var registerClicked = false
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = { registerClicked = true }
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.register)).performClick()
        assert(registerClicked)
    }

    @Test
    fun registerContent_showsLoading_whenLoadingStateIsTrue() {
        composeTestRule.setContent {
            RegisterContent(
                uiState = RegisterUiState(isLoading = true),
                onFirstNameChanged = {},
                onLastNameChanged = {},
                onAddressChanged = {},
                onPhoneNumberChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRegisterClick = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.register)).assertDoesNotExist()
    }
}
