package com.xando.auth.sign_up.pages.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.auth.sign_up.components.SignUpPage
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.extendedColors
import com.xando.design.ui.theme.getString
import com.xando.feature.auth.R
import com.xando.core.design.R as RDesign

private const val LOGIN_MAX_LENGTH = 64

/** Размер индикатора проверки логина в поле ввода. */
private val AvailabilityIndicatorSize = 20.dp

/** Толщина индикатора проверки логина в поле ввода. */
private val AvailabilityIndicatorStrokeWidth = 2.dp

/**
 * Экран ввода логина.
 */
@Composable
internal fun SignUpLoginScreen(onContinue: () -> Unit) {
    val viewModel = hiltViewModel<SignUpLoginViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    SignUpLoginEvent.NavigateNext -> onContinue()
                }
            }
    }

    val isChecking = uiState.availability == LoginAvailability.CHECKING
    val isAvailable = uiState.availability == LoginAvailability.AVAILABLE

    // Текст под полем пишет только сервер. Локальные правила краснят рамку пустой ошибкой:
    // сообщение для них — подсвеченная подсказка под полем.
    val loginErrorText = uiState.loginError?.getString() ?: "".takeIf { uiState.isLoginRulesViolated }

    SignUpPage(
        title = stringResource(R.string.auth_sign_up_login_title),
        onContinue = { viewModel.onContinueClick() },
        currentStep = 3,
        action = uiState.action,
        isLoading = isChecking,
    ) {
        StayaOutlinedTextField(
            value = uiState.login,
            onValueChange = { viewModel.updateLogin(it) },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.auth_sign_up_login_label),
            placeholder = stringResource(R.string.auth_sign_up_login_placeholder),
            maxLength = LOGIN_MAX_LENGTH,
            trailingIcon = when {
                isChecking -> {
                    {
                        CircularProgressIndicator(
                            modifier = Modifier.size(AvailabilityIndicatorSize),
                            strokeWidth = AvailabilityIndicatorStrokeWidth
                        )
                    }
                }

                isAvailable -> {
                    {
                        Icon(
                            painter = painterResource(RDesign.drawable.design_ic_check_24px),
                            contentDescription = stringResource(R.string.auth_sign_up_login_available_content_description)
                        )
                    }
                }

                else -> null
            },
            errorText = loginErrorText,
            isSuccess = isAvailable,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.auth_sign_up_login_hint, uiState.minLoginLength),
            style = MaterialTheme.typography.bodyMedium,
            // Локальные правила не пишут ошибку под полем: вместо неё краснеет эта подсказка.
            color = if (uiState.isLoginRulesViolated) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.extendedColors.textColor
            },
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
