package com.yustar.core.presentation.widget

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.yustar.core.ui.widget.TextInput
import org.junit.Rule
import org.junit.Test

class TextInputTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun textInput_passwordToggle_changesIconAndDescription() {
        // Given a TextInput with password enabled
        composeTestRule.setContent {
            TextInput(
                label = "Password",
                placeHolder = "Enter password",
                isPassword = true
            )
        }

        // Initially, the icon should be "Show password"
        composeTestRule.onNodeWithContentDescription("Show password")
            .assertIsDisplayed()

        // When the toggle is clicked
        composeTestRule.onNodeWithContentDescription("Show password")
            .performClick()

        // Then the icon should change to "Hide password"
        composeTestRule.onNodeWithContentDescription("Hide password")
            .assertIsDisplayed()

        // When the toggle is clicked again
        composeTestRule.onNodeWithContentDescription("Hide password")
            .performClick()

        // Then the icon should change back to "Show password"
        composeTestRule.onNodeWithContentDescription("Show password")
            .assertIsDisplayed()
    }
}