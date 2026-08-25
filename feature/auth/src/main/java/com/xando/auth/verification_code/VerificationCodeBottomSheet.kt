package com.xando.auth.verification_code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.getString
import com.xando.feature.auth.R

/**
 * Шторка ввода кода подтверждения e-mail.
 *
 * @param isLoading Флаг выполнения запроса. Это состояние, поэтому едет параметром.
 *   Ошибки сервера параметром не ходят: их доставляет ViewModel потоком через DI, иначе значение
 *   переигрывалось бы при каждом входе в композицию и возвращало ошибку, снятую вводом кода.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VerificationCodeBottomSheet(
    onCodeConfirmed: (String) -> Unit,
    onResendCode: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    isLoading: Boolean = false,
) {
    val viewModel = hiltViewModel<VerificationCodeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var hasInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isLoading) {
        viewModel.updateLoading(isLoading)
    }

    LaunchedEffect(Unit) {
        if (!hasInitialized) {
            viewModel.onBottomSheetShown()
            hasInitialized = true
        }

        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is VerificationCodeEvent.CodeConfirmed -> onCodeConfirmed(event.code)
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        VerificationCodeBottomSheetContent(
            state = uiState,
            onValueChange = viewModel::onCodeChanged,
            onResendCode = {
                if (viewModel.onResendCodeClick()) {
                    onResendCode()
                }
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
internal fun VerificationCodeBottomSheetContent(
    state: VerificationCodeUiState,
    onValueChange: (String) -> Unit,
    onResendCode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.auth_verification_code_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        StayaOutlinedTextField(
            value = state.code,
            onValueChange = onValueChange,
            label = stringResource(R.string.auth_verification_code_code_label),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            errorText = state.errorText?.getString(),
            enabled = !state.isLoading,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isTimerRunning) {
            val minutes = state.timeLeftSeconds / 60
            val seconds = state.timeLeftSeconds % 60
            Text(
                text = stringResource(R.string.auth_verification_code_timer_text, minutes, seconds),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.auth_verification_code_resend_text),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = onResendCode,
                    enabled = !state.isLoading,
                    contentPadding = PaddingValues(start = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.auth_verification_code_resend_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
