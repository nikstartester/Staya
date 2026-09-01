package com.xando.auth.forgot_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.auth.components.EmailTextField
import com.xando.auth.components.PasswordTextField
import com.xando.auth.verification_code.VerificationCodeBottomSheet
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.snackbar.LocalSnackbarController
import com.xando.design.ui.theme.extendedColors
import com.xando.design.ui.theme.getString
import com.xando.feature.auth.R
import com.xando.core.design.R as RDesign

/**
 * Экран сброса пароля.
 *
 * @param onBackClick Возврат на предыдущий экран.
 * @param onPasswordResetSuccess Переход на экран входа после успешной смены пароля.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onPasswordResetSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<ForgotPasswordViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackbarController = LocalSnackbarController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is ForgotPasswordEvent.ShowSnackbar -> snackbarController.show(event.snackbarData)

                    ForgotPasswordEvent.NavigateToLogin -> onPasswordResetSuccess()
                }
            }
    }

    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.auth_forgot_password_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        onBackClick()
                    }) {
                        Icon(
                            painter = painterResource(RDesign.drawable.design_ic_arrow_back_24dp),
                            contentDescription = stringResource(R.string.auth_back_content_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        ForgotPasswordContent(
            state = uiState,
            onEmailChange = viewModel::updateEmail,
            onPasswordChange = viewModel::updatePassword,
            onRepeatPasswordChange = viewModel::updateRepeatPassword,
            onPasswordVisibilityChange = viewModel::updatePasswordVisibility,
            onContinueClick = {
                focusManager.clearFocus()
                viewModel.onContinueClick()
            },
            modifier = Modifier.padding(paddingValues)
        )

        if (uiState.isVerificationCodeBottomSheetVisible) {
            VerificationCodeBottomSheet(
                onCodeConfirmed = viewModel::onVerificationCodeConfirmed,
                onResendCode = viewModel::onResendVerificationCode,
                onDismiss = viewModel::hideVerificationCodeBottomSheet,
                isLoading = uiState.isVerificationLoading
            )
        }
    }
}

/**
 * Форма сброса пароля: e-mail аккаунта и новый пароль.
 */
@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.auth_forgot_password_form_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.extendedColors.textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(36.dp))

        EmailTextField(
            value = state.email,
            label = stringResource(R.string.auth_sign_up_email_password_email_label),
            onValueChanged = onEmailChange,
            readOnly = false,
            errorText = state.emailError?.getString()
        )

        Spacer(modifier = Modifier.height(24.dp))

        PasswordTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            readOnly = false,
            label = stringResource(R.string.auth_forgot_password_new_password_label),
            showPasswordButtonVisible = state.showPasswordButtonVisible,
            isPasswordVisible = state.isPasswordVisible,
            onPasswordButtonClicked = onPasswordVisibilityChange,
            errorText = state.passwordError?.getString()
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            value = state.repeatPassword,
            onValueChange = onRepeatPasswordChange,
            readOnly = false,
            label = stringResource(R.string.auth_sign_up_email_password_repeat_password_label),
            showPasswordButtonVisible = state.showPasswordButtonVisible,
            isPasswordVisible = state.isPasswordVisible,
            onPasswordButtonClicked = onPasswordVisibilityChange,
            errorText = state.repeatPasswordError?.getString()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.auth_sign_up_email_password_hint, state.minPasswordLength),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))

        StayaButton(
            text = stringResource(R.string.auth_forgot_password_continue_title),
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth(),
            isLoading = state.isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
