package com.yustar.dashboard.presentation.widget

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yustar.core.ui.theme.SosmedTheme
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class PostMoreDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun postMoreDialog_whenShowMenuIsTrue_displaysAllItems() {
        composeTestRule.setContent {
            SosmedTheme {
                PostMoreDialog(showMenu = true, onDismissRequest = {})
            }
        }

        composeTestRule.onNodeWithText("Why you're seeing this").assertIsDisplayed()
        composeTestRule.onNodeWithText("Not interested").assertIsDisplayed()
        composeTestRule.onNodeWithText("Report").assertIsDisplayed()
    }

    @Test
    fun postMoreDialog_onItemClick_callsDismissRequest() {
        val onDismissRequest: (Boolean) -> Unit = mockk(relaxed = true)

        composeTestRule.setContent {
            SosmedTheme {
                PostMoreDialog(showMenu = true, onDismissRequest = onDismissRequest)
            }
        }

        composeTestRule.onNodeWithText("Not interested").performClick()
        
        verify { onDismissRequest(false) }
        confirmVerified(onDismissRequest)
    }
}
