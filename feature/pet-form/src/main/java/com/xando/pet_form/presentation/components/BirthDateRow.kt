package com.xando.pet_form.presentation.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.xando.design.ui.components.text_field.DateVisualTransformation
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.StayaString
import com.xando.design.ui.theme.getString
import com.xando.feature.pet_form.R
import com.xando.core.design.R as RDesign

/**
 * Поле даты рождения. Дата вводится с цифровой клавиатуры, точки между днём, месяцем и годом
 * дорисовывает [DateVisualTransformation]; иконка календаря справа открывает выбор даты.
 *
 * @param value Введённые цифры даты без разделителей.
 * @param error Ошибка поля.
 * @param onValueChange Обработчик ввода.
 * @param onCalendarClick Обработчик нажатия на иконку календаря.
 */
@Composable
internal fun BirthDateRow(
    value: String,
    error: StayaString?,
    onValueChange: (String) -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    StayaOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = stringResource(R.string.pet_form_birth_date_label),
        placeholder = stringResource(R.string.pet_form_birth_date_placeholder),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DateVisualTransformation,
        errorText = error?.getString(),
        trailingIcon = {
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    onCalendarClick()
                }
            ) {
                Icon(
                    painter = painterResource(RDesign.drawable.design_ic_calendar_today_24px),
                    contentDescription = stringResource(R.string.pet_form_birth_date_calendar)
                )
            }
        },
        modifier = modifier
    )
}