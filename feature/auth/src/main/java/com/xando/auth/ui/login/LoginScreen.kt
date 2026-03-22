package com.xando.auth.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**@SelfDocumented*/
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: (prefilledEmail: String?) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (state) {
        is LoginUiState.Success -> {
            onLoginSuccess()
        }

        is LoginUiState.Idle,
        is LoginUiState.Loading,
        is LoginUiState.Error -> {
            LoginContent(
                state = state,
                onEmailChange = viewModel::onEmailChanged,
                onPasswordChange = viewModel::onPasswordChanged,
                onLoginClick = viewModel::onLoginClick,
                onSignUpClick = onSignUpClick,
                onForgotPasswordClick = onForgotPasswordClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoginContent(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onSignUpClick: (String?) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val credentials = when (state) {
        is LoginUiState.Idle -> state.loginCredentials
        is LoginUiState.Loading -> state.loginCredentials
        is LoginUiState.Error -> state.loginCredentials
        else -> LoginCredentials()
    }

    val isLoading = state is LoginUiState.Loading
    val isLoginEnabled =
        (state as? LoginUiState.Idle)?.isLoginEnabled == true

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
                email = credentials.login,
                password = credentials.password,
                isLoading = isLoading,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onForgotPasswordClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            LoginActions(
                isLoading = isLoading,
                isLoginEnabled = isLoginEnabled,
                email = credentials.login,
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick
            )

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}