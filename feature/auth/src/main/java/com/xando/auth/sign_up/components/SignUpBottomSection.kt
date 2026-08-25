package com.xando.auth.sign_up.components

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.components.button.StayaOutlinedButton
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.auth.R

private const val TOTAL_STEPS = 4

/**
 * Нижняя секция signup-экрана с прогрессом и основной кнопкой действия.
 *
 * @param isLoading Флаг выполнения запроса. При `true` на кнопке действия отображается индикатор загрузки.
 */
@Composable
internal fun SignUpBottomSection(
    @IntRange(from = 1, to = TOTAL_STEPS.toLong())
    currentStep: Int,
    action: BottomSectionAction,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    require(currentStep in 1..TOTAL_STEPS)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.auth_sign_up_step_title, currentStep, TOTAL_STEPS),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.extendedColors.unaccentedTextColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        if (action == BottomSectionAction.CONTINUE) {
            StayaButton(
                text = stringResource(R.string.auth_sign_up_continue_title),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading
            )
        } else {
            StayaOutlinedButton(
                text = stringResource(R.string.auth_sign_up_skip_title),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading
            )
        }
    }
}

/**
 * Действия нижней панели.
 */
internal enum class BottomSectionAction {
    CONTINUE,
    SKIP
}
