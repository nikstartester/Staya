package com.xando.pet_form.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xando.design.ui.theme.extendedColors

/**
 * Заголовок секции формы питомца.
 *
 * @param text Текст заголовка.
 * @param color Цвет текста; секция с ошибкой передаёт сюда цвет ошибки.
 */
@Composable
internal fun FormSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.extendedColors.primaryTextColor,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = color,
        modifier = modifier,
    )
}
