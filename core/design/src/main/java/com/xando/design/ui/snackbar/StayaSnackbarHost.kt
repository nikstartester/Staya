package com.xando.design.ui.snackbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xando.design.ui.theme.extendedColors

/**
 * Хост для отображения стилизованных снекбаров приложения.
 *
 * Поддерживает свайп для закрытия. Отображает только [StayaSnackbarData],
 * остальные типы [SnackbarVisuals][androidx.compose.material3.SnackbarVisuals] игнорируются.
 */
@Composable
fun StayaSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        // Показываем только наши снекбары
        val visuals = data.visuals as? StayaSnackbarData ?: return@SnackbarHost

        val dismissState = rememberSwipeToDismissBoxState()

        LaunchedEffect(dismissState.currentValue) {
            if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                data.dismiss()
            }
        }

        val containerColor = when (visuals.type) {
            SnackbarType.ERROR -> MaterialTheme.extendedColors.dangerColor
            SnackbarType.SUCCESS -> MaterialTheme.extendedColors.successColor
            SnackbarType.ACCENT -> MaterialTheme.extendedColors.primaryColor
        }

        val message = visuals.message.takeIf { it.isNotBlank() } ?: stringResource(visuals.messageResId)

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {},
        ) {
            Surface(
                color = containerColor,
                shape = MaterialTheme.shapes.small,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    visuals.iconRes?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.contrastIconColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.extendedColors.contrastTextColor
                    )
                }
            }
        }
    }
}