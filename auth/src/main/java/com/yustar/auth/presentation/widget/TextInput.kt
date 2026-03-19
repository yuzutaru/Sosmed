package com.yustar.auth.presentation.widget

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yustar.core.ui.theme.SosmedTheme

/**
 * Created by Yustar Pramudana on 06/03/26.
 */

@Composable
fun TextInput(
    label: String,
    value: String = "",
    placeHolder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (String) -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = label
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = {
                if (keyboardType == KeyboardType.Number || keyboardType == KeyboardType.Phone) {
                    if (it.all { char -> char.isDigit() }) {
                        onValueChange(it)
                    }
                } else {
                    onValueChange(it)
                }
            },
            placeholder = {
                if (value.isEmpty())
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = placeHolder
                    )
            },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    // Please provide localized description for accessibility services
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                }
            }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, device = Devices.PIXEL_4)
@Composable
fun NightModePreviewTextInput() {
    SosmedTheme {
        TextInput(
            stringResource(com.yustar.auth.R.string.email),
            "",
            stringResource(com.yustar.auth.R.string.input_email)
        ) {}
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, device = Devices.PIXEL_4)
@Composable
fun LightModePreviewTextInput() {
    SosmedTheme {
        TextInput(
            stringResource(com.yustar.auth.R.string.password),
            "",
            stringResource(com.yustar.auth.R.string.input_password),
            isPassword = true
        ) {}
    }
}
