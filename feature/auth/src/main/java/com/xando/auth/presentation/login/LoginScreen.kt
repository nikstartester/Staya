package com.xando.auth.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.design.ui.theme.getString
import kotlinx.coroutines.launch

/**@SelfDocumented*/
@Composable
internal fun LoginScreen(
    viewModel: LoginViewModel,
    onSignUpClick: (prefilledEmail: String?) -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is LoginEvent.ShowError -> {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        scope.launch {
                            // TODO: добавить разнообразные цветные снекбары (SUCCESS, ERROR, INFO)
                            snackbarHostState.showSnackbar(
                                message = event.message.getString(context),
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                }
            }
    }

    LoginContent(
        state = state,
        onEmailChange = viewModel::onEmailChanged,
        onPasswordChange = viewModel::onPasswordChanged,
        onLoginClick = {
            focusManager.clearFocus()
            viewModel.onLoginClick()
        },
        onSignUpClick = onSignUpClick,
        onForgotPasswordClick = onForgotPasswordClick,
        modifier = modifier
    )

    Popup(alignment = Alignment.TopCenter, properties = PopupProperties(focusable = false)) {
        SnackbarHost(hostState = snackbarHostState)
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
    val credentials = state.loginCredentials
    val isLoading = state.isLoading
    val loginError = state.loginError?.getString()
    val passwordError = state.passwordError?.getString()

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
                loginError = loginError,
                passwordError = passwordError,
                isLoading = isLoading,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onForgotPasswordClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.weight(1f))

            LoginActions(
                isLoading = isLoading,
                email = credentials.login,
                onLoginClick = onLoginClick,
                onSignUpClick = onSignUpClick
            )

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}