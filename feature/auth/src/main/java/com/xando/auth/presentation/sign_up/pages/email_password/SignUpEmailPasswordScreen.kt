package com.xando.auth.presentation.sign_up.pages.email_password

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.auth.presentation.components.EmailTextField
import com.xando.auth.presentation.components.OtpBottomSheet
import com.xando.auth.presentation.components.PasswordTextField
import com.xando.auth.presentation.sign_up.components.BottomSectionAction
import com.xando.auth.presentation.sign_up.components.SignUpPage
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.auth.R

/**
 * Экран ввода e-mail и пароля.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignUpEmailPasswordScreen(onContinue: () -> Unit) {
    val viewModel = hiltViewModel<SignUpEmailPasswordViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    SignUpEmailPasswordEvent.NavigateNext -> onContinue()
                }
            }
    }

    SignUpPage(
        title = stringResource(R.string.auth_sign_up_email_password_title),
        onContinue = { viewModel.onContinueClick() },
        currentStep = 4,
        action = BottomSectionAction.CONTINUE,
    ) {
        EmailTextField(
            value = uiState.email,
            label = stringResource(R.string.auth_sign_up_email_password_email_label),
            onValueChanged = { viewModel.updateEmail(it) },
            readOnly = false,
            errorText = uiState.emailError,
        )

        Spacer(Modifier.height(24.dp))

        PasswordTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            readOnly = false,
            label = stringResource(R.string.auth_sign_up_email_password_password_label),
            showPasswordButtonVisible = uiState.showPasswordButtonVisible,
            isPasswordVisible = uiState.isPasswordVisible,
            onPasswordButtonClicked = { viewModel.updatePasswordVisibility(it) },
            errorText = uiState.passwordError,
        )

        Spacer(Modifier.height(8.dp))

        PasswordTextField(
            value = uiState.repeatPassword,
            onValueChange = { viewModel.updateRepeatPassword(it) },
            readOnly = false,
            label = stringResource(R.string.auth_sign_up_email_password_repeat_password_label),
            showPasswordButtonVisible = uiState.showPasswordButtonVisible,
            isPasswordVisible = uiState.isPasswordVisible,
            onPasswordButtonClicked = { viewModel.updatePasswordVisibility(it) },
            errorText = uiState.repeatPasswordError,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.auth_sign_up_email_password_hint, uiState.minPasswordLength),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.isOtpSheetVisible) {
            OtpBottomSheet(
                onCodeConfirmed = { code -> viewModel.verifyOtp(code) },
                onResendCode = { viewModel.resendOtp() },
                onDismiss = { viewModel.dismissOtp() },
                isLoading = uiState.isOtpLoading,
                errorText = uiState.otpError
            )
        }
    }
}
