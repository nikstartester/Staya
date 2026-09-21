package com.xando.pet_form.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.feature.pet_form.R

private const val ABOUT_MAX_LENGTH = 700
private const val ABOUT_MIN_LINES = 4
private const val ABOUT_MAX_LINES = 6

/**
 * Секция описания питомца: заголовок и многострочное поле свободного текста.
 *
 * @param value Текст описания.
 * @param onValueChange Обработчик ввода.
 */
@Composable
internal fun AboutSection(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FormSectionTitle(text = stringResource(R.string.pet_form_about_label))

        Spacer(modifier = Modifier.height(12.dp))

        StayaOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(R.string.pet_form_about_placeholder),
            minLines = ABOUT_MIN_LINES,
            maxLines = ABOUT_MAX_LINES,
            maxLength = ABOUT_MAX_LENGTH,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}