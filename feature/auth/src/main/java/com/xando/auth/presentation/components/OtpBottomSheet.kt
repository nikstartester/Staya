package com.xando.auth.presentation.components

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.feature.auth.R
import kotlinx.coroutines.delay

/**
 * Bottom sheet для подтверждения OTP кода.
 *
 * @param onCodeConfirmed Вызывается при нажатии кнопки подтверждения с введенным кодом
 * @param onResendCode Вызывается при нажатии кнопки "Отправить снова"
 * @param onDismiss Вызывается при закрытии шторки
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpBottomSheet(
    onCodeConfirmed: (String) -> Unit,
    onResendCode: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    isLoading: Boolean = false,
    errorText: String? = null,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        OtpBottomSheetContent(
            onCodeConfirmed = onCodeConfirmed,
            onResendCode = onResendCode,
            isLoading = isLoading,
            errorText = errorText,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
fun OtpBottomSheetContent(
    onCodeConfirmed: (String) -> Unit,
    onResendCode: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorText: String? = null,
) {
    var code by remember { mutableStateOf("") }
    var timeLeft by remember { mutableIntStateOf(60) }
    val isTimerRunning = timeLeft > 0

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.auth_otp_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        StayaOutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                    code = it
                }
            },
            label = stringResource(R.string.auth_otp_code_label),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            errorText = errorText,
            enabled = !isLoading,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isTimerRunning) {
            val minutes = timeLeft / 60
            val seconds = timeLeft % 60
            Text(
                text = stringResource(R.string.auth_otp_timer_text, minutes, seconds),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.auth_otp_resend_text),
                    style = MaterialTheme.typography.bodyMedium
                )
                TextButton(
                    onClick = {
                        timeLeft = 60
                        onResendCode()
                    },
                    enabled = !isLoading,
                    contentPadding = PaddingValues(start = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.auth_otp_resend_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        StayaButton(
            text = stringResource(R.string.auth_sign_up_continue_title),
            modifier = Modifier.fillMaxWidth(),
            onClick = { onCodeConfirmed(code) },
            enabled = code.length == 4 && !isLoading,
            isLoading = isLoading
        )
    }
}
