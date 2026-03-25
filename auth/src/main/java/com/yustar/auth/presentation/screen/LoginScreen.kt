package com.yustar.auth.presentation.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yustar.auth.R
import com.yustar.auth.presentation.event.LoginUiEvent
import com.yustar.auth.presentation.state.LoginUiState
import com.yustar.auth.presentation.viewmodel.LoginViewModel
import com.yustar.auth.presentation.widget.TextInput
import com.yustar.core.ui.theme.Red60
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.core.ui.theme.Turquoise25

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        if (uiState.error.isNotEmpty()) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Pass state and events down to a stateless content Composable
    LoginContent(
        uiState = uiState,
        onEmailChanged = { viewModel.onEvent(LoginUiEvent.OnEmailChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(LoginUiEvent.OnPasswordChanged(it)) },
        onLogin = { viewModel.login { onLoginSuccess() } },
        onRegisterClick = onRegisterClick
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                text = stringResource(R.string.login_to_your_account)
            )

            Spacer(modifier = Modifier.height(100.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
            ) {
                TextInput(
                    stringResource(R.string.email),
                    uiState.email, stringResource(R.string.input_email),
                    onValueChange = onEmailChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInput(
                    stringResource(R.string.password),
                    uiState.password, stringResource(R.string.input_password),
                    isPassword = true,
                    onValueChange = onPasswordChanged
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Turquoise25),
                enabled = !uiState.isLoading,
                onClick = onLogin,
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth().height(40.dp).padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp).width(24.dp),
                                color = MaterialTheme.colorScheme.background
                            )
                        } else {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.background,
                                text = stringResource(R.string.login)
                            )
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = stringResource(R.string.dont_have_an_account)
                )

                Button(
                    modifier = Modifier.width(110.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Transparent),
                    contentPadding = PaddingValues(0.dp),
                    onClick = onRegisterClick
                ) {
                    Column(
                        modifier = Modifier.padding(2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelMedium,
                            color = Red60,
                            text = stringResource(R.string.register)
                        )
                    }
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewLoginScreen() {
    SosmedTheme() {
        LoginContent(
            uiState = LoginUiState(email = "test@example.com"), {},
            {}, {}, {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewLoginScreen() {
    SosmedTheme() {
        LoginContent(
            uiState = LoginUiState(email = "test@example.com"), {},
            {}, {}, {}
        )
    }
}
