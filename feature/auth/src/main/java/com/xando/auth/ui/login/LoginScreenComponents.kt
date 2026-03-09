package com.xando.auth.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xando.auth.ui.components.EmailTextField
import com.xando.auth.ui.components.PasswordTextField
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.components.button.StayaOutlinedButton
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.auth.R

/**
 * Компонент заголовка с лого и названием
 */
@Composable
internal fun LoginHeader() {
    Column(
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
internal fun LoginForm(
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
            label = stringResource(R.string.auth_email_or_login_title),
            onValueChanged = onEmailChange,
            readOnly = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = password,
            onValueChange = onPasswordChange,
            readOnly = isLoading
        )

        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
            enabled = !isLoading
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
internal fun LoginActions(
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
        StayaButton(
            text = stringResource(R.string.auth_login_title),
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(),
            isLoading = isLoading
        )

        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(R.string.auth_or_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.unaccentedTextColor
        )

        StayaOutlinedButton(
            text = stringResource(R.string.auth_sign_up_title),
            onClick = { onSignUpClick(email.takeIf { it.isNotBlank() }) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
    }
}