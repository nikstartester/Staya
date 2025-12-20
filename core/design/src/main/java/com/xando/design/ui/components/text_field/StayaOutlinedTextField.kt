package com.xando.design.ui.components.text_field

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.xando.design.ui.theme.extendedColors

/**
 * Объект, содержащий дефолтные значения для компонента [StayaOutlinedTextField]
 */
object StayaTextFieldDefaults {

    /**
     * Стиль текста по умолчанию
     */
    val textStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.bodyLarge

    /**
     * Форма углов по умолчанию
     */
    val shape: Shape
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.shapes.small
}

/**
 * Стандартизированное текстовое поле с контуром
 *
 * @param value Текущее значение текстового поля
 * @param onValueChange Callback, вызываемый при изменении текста пользователем
 * @param modifier Модификатор для кастомизации компонента
 * @param label Текст метки для поля ввода
 * @param enabled Определяет, активно ли поле
 * @param readOnly Если `true`, текст можно выделить и скопировать, но нельзя редактировать
 * @param singleLine Ограничивает поле одной строкой текста
 * @param maxLines Максимальное количество видимых строк
 * @param leadingIcon Composable для иконки в начале поля. Автоматически окрашивается в зависимости от состояния.
 * @param trailingIcon Composable для иконки в конце поля. Автоматически окрашивается в зависимости от состояния.
 * @param visualTransformation Трансформация отображения текста
 * @param keyboardOptions Конфигурация софт-клавиатуры
 * @param keyboardActions Обработчики событий IME actions клавиатуры
 * @param errorText Текст ошибки валидации. При установке:
 *      - Поле переходит в error state (красный цвет границы, label и иконок (если цвет не определен извне))
 *      - Если не пустой, текст отображается под полем
 */
@Composable
fun StayaOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    errorText: String? = null
) {
    val hasError = !errorText.isNullOrBlank()
    val errorColor = MaterialTheme.colorScheme.error
    // Определяем цвет иконок в зависимости от состояния
    val iconColor = when {
        hasError -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.extendedColors.primaryIconColor
    }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        textStyle = StayaTextFieldDefaults.textStyle,
        shape = StayaTextFieldDefaults.shape,
        leadingIcon = leadingIcon?.let {
            {
                CompositionLocalProvider(LocalContentColor provides iconColor) { it() }
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                CompositionLocalProvider(LocalContentColor provides iconColor) { it() }
            }
        },
        supportingText =
            if (hasError) {
                { Text(errorText, color = errorColor) }
            } else null,
        isError = errorText != null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}