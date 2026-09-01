package com.xando.design.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xando.design.ui.theme.extendedColors

/**
 * Диалог подтверждения действия с двумя кнопками.
 *
 * @param title Заголовок диалога.
 * @param confirmText Текст кнопки подтверждения.
 * @param dismissText Текст кнопки отказа.
 * @param onConfirm Обработчик подтверждения.
 * @param onDismiss Обработчик отказа. Вызывается и при закрытии диалога нажатием мимо него или кнопкой «Назад».
 * @param modifier Модификатор для кастомизации компонента.
 * @param text Пояснение под заголовком. Если `null`, диалог состоит только из заголовка и кнопок.
 */
//TODO: Пока цвета кнопок захардкожены (primaryTextColor, dangerColor), нужно сделать апи для кастомизации кнопок
@Composable
fun StayaConfirmationDialog(
    title: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.extendedColors.textColor
            )
        },
        text = text?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.extendedColors.textColor
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.extendedColors.dangerColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.extendedColors.primaryTextColor
                )
            }
        }
    )
}
