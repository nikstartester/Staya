package com.xando.auth.ui.login

import android.preference.PreferenceManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.xando.auth.ui.components.PasswordTextField
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.auth.R
import com.xando.core.design.R as RDesign

/**@SelfDocumented*/
@Composable
internal fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: (prefilledEmail: String?) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // TODO: все это уйдет в viewModel
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            LoginHeader()

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            LoginForm(
                email = email,
                password = password,
                isLoading = isLoading,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onForgotPasswordClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            LoginActions(
                isLoading = isLoading,
                email = email,
                onLoginClick = {
                    isLoading = true
                    PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean("IS_LOGIN", true) }
                    onLoginSuccess()
                },
                onSignUpClick = onSignUpClick
            )

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

/**
 * Компонент заголовка с лого и названием
 */
@Composable
private fun LoginHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.auth_app_icon),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.auth_app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.extendedColors.primaryTextColor
        )
    }
}

/**
 * Форма ввода email и пароля
 */
@Composable
private fun LoginForm(
    email: String,
    password: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        EmailTextField(
            value = email,
            onValueChanged = onEmailChange,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            enabled = !isLoading
        )

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.auth_forgot_password_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.extendedColors.primaryTextColor
            )
        }
    }
}

/**
 * Кнопки действий - вход и регистрация
 */
@Composable
private fun LoginActions(
    isLoading: Boolean,
    email: String,
    onLoginClick: () -> Unit,
    onSignUpClick: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_login_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.extendedColors.contrastTextColor
                )
            }
        }

        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(R.string.auth_or_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.unaccentedTextColor
        )

        OutlinedButton(
            onClick = { onSignUpClick(email.takeIf { it.isNotBlank() }) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            enabled = !isLoading
        ) {
            Text(
                text = stringResource(R.string.auth_sign_up_title),
                color = MaterialTheme.extendedColors.primaryTextColor,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun EmailTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    enabled: Boolean
) {
    StayaOutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = stringResource(R.string.auth_email_or_login_title),
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(RDesign.drawable.design_ic_mail_24dp),
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, hintLocales = LocaleList("en"))
    )
}