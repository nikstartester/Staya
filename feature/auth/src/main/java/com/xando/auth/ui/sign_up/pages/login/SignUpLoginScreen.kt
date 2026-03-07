package com.xando.auth.ui.sign_up.pages.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xando.auth.ui.sign_up.components.SignUpPage
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.auth.R

@Composable
internal fun SignUpLoginScreen(
    onContinue: () -> Unit,
) {
    val viewModel = hiltViewModel<SignUpLoginViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SignUpLoginViewModel.Event.NavigateNext -> onContinue()
            }
        }
    }

    SignUpPage(
        title = stringResource(R.string.auth_sign_up_login_title),
        onContinue = { viewModel.onContinueClick() },
        currentStep = 3,
        action = uiState.action,
    ) {
        StayaOutlinedTextField(
            value = uiState.login,
            onValueChange = { viewModel.updateLogin(it) },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.auth_sign_up_login_label),
            placeholder = stringResource(R.string.auth_sign_up_login_placeholder),
            errorText = uiState.loginError,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.auth_sign_up_login_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.textColor,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
