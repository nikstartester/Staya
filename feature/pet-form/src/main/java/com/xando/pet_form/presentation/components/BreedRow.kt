package com.xando.pet_form.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.xando.core.models.pet.PetBreed
import com.xando.core.pet_dictionary.labelRes
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.StayaString
import com.xando.design.ui.theme.getString
import com.xando.feature.pet_form.R
import com.xando.core.design.R as RDesign

/**
 * Поле породы. Ввода с клавиатуры нет: поле только показывает выбранную породу, а нажатие на него
 * открывает экран выбора.
 *
 * @param breed Выбранная порода; `null`, пока порода не выбрана.
 * @param error Ошибка поля.
 * @param onClick Обработчик нажатия на поле.
 */
@Composable
internal fun BreedRow(
    breed: PetBreed?,
    error: StayaString?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OnlyClickableOutlinedTextField(onClick = onClick) {
        StayaOutlinedTextField(
            value = stringResource(breed?.labelRes ?: R.string.pet_form_breed_placeholder),
            onValueChange = {},
            modifier = modifier.fillMaxWidth(),
            label = stringResource(R.string.pet_form_breed_label),
            readOnly = true,
            singleLine = true,
            errorText = error?.getString(),
            trailingIcon = {
                Icon(
                    painter = painterResource(RDesign.drawable.design_ic_search_24px),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
private fun OnlyClickableOutlinedTextField(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        content()
        Box(
            modifier = modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    }
}