package com.yustar.auth.presentation.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yustar.auth.R
import com.yustar.auth.presentation.event.RegisterUiEvent
import com.yustar.auth.presentation.state.RegisterUiState
import com.yustar.auth.presentation.viewmodel.RegisterViewModel
import com.yustar.core.ui.theme.Red60
import com.yustar.core.ui.theme.SosmedTheme
import com.yustar.core.ui.widget.TextInput
import org.koin.androidx.compose.koinViewModel

/**
 * Created by Yustar Pramudana on 07/03/26.
 */

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = koinViewModel(),
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Registration Success!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess()
            viewModel.resetSuccessState()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error.isNotEmpty()) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    RegisterContent(
        uiState = uiState,
        onFirstNameChanged = { viewModel.onEvent(RegisterUiEvent.OnFirstNameChanged(it)) },
        onLastNameChanged = { viewModel.onEvent(RegisterUiEvent.OnLastNameChanged(it)) },
        onAddressChanged = { viewModel.onEvent(RegisterUiEvent.OnAddressChanged(it)) },
        onPhoneNumberChanged = { viewModel.onEvent(RegisterUiEvent.OnPhoneNumberChanged(it)) },
        onEmailChanged = { viewModel.onEvent(RegisterUiEvent.OnUsernameChanged(it)) },
        onPasswordChanged = { viewModel.onEvent(RegisterUiEvent.OnPasswordChanged(it)) },
        onRegisterClick = { viewModel.register() }
    )
}

@Composable
fun RegisterContent(
    uiState: RegisterUiState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
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
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                text = stringResource(R.string.create_new_account)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                TextInput(
                    stringResource(R.string.first_name),
                    uiState.firstName, stringResource(R.string.input_first_name),
                    onValueChange = onFirstNameChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInput(
                    stringResource(R.string.last_name),
                    uiState.lastName, stringResource(R.string.input_last_name),
                    onValueChange = onLastNameChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInput(
                    stringResource(R.string.address),
                    uiState.address, stringResource(R.string.input_address),
                    onValueChange = onAddressChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInput(
                    stringResource(R.string.phone_number),
                    uiState.phoneNumber, stringResource(R.string.input_phone_number),
                    keyboardType = KeyboardType.Phone,
                    onValueChange = onPhoneNumberChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInput(
                    stringResource(R.string.email_or_username),
                    uiState.username, stringResource(R.string.input_email),
                    onValueChange = onEmailChanged
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInput(
                    stringResource(R.string.password),
                    uiState.password, stringResource(R.string.input_password),
                    isPassword = true,
                    onValueChange = onPasswordChanged
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Red60),
                    enabled = !uiState.isLoading,
                    onClick = onRegisterClick,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .width(24.dp),
                                    color = MaterialTheme.colorScheme.background
                                )
                            } else {
                                Text(
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.background,
                                    text = stringResource(R.string.register)
                                )
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewRegisterScreen() {
    SosmedTheme() {
        RegisterContent(uiState = RegisterUiState(), {},
            {}, {}, {},
            {}, {}, {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewRegisterScreen() {
    SosmedTheme() {
        RegisterContent(uiState = RegisterUiState(), {},
            {}, {}, {},
            {}, {}, {}
        )
    }
}
