package com.xando.pet_form.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xando.core.models.pet.PetSex
import com.xando.design.ui.theme.StayaString
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.pet_form.R

/**
 * Выбор пола питомца: заголовок и две кнопки-переключателя в ряд. При ошибке заголовок
 * подсвечивается цветом ошибки.
 *
 * @param selected Выбранный пол; `null`, пока пол не выбран.
 * @param error Ошибка поля.
 * @param onSelect Вызывается для нажатого пола.
 */
@Composable
internal fun SexPicker(
    selected: PetSex?,
    error: StayaString?,
    onSelect: (PetSex) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FormSectionTitle(
            text = stringResource(R.string.pet_form_sex_label),
            modifier = Modifier.padding(bottom = 8.dp),
            color = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.extendedColors.primaryTextColor,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SexOption(
                label = stringResource(R.string.pet_form_sex_male),
                isSelected = selected == PetSex.MALE,
                onClick = { onSelect(PetSex.MALE) },
                modifier = Modifier.weight(1f)
            )
            SexOption(
                label = stringResource(R.string.pet_form_sex_female),
                isSelected = selected == PetSex.FEMALE,
                onClick = { onSelect(PetSex.FEMALE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private val SexOptionHeight = 44.dp

@Composable
private fun SexOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.extendedColors.primaryColor
        else MaterialTheme.extendedColors.outlineColor
    )
    val containerColor by animateColorAsState(
        if (isSelected) MaterialTheme.extendedColors.secondaryBackgroundColor
        else MaterialTheme.extendedColors.secondaryBackgroundColor.copy(alpha = 0f)
    )
    val contentColor by animateColorAsState(
        if (isSelected) MaterialTheme.extendedColors.secondaryBackgroundTextColor
        else MaterialTheme.extendedColors.textColor
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(SexOptionHeight),
        shape = CircleShape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = containerColor, contentColor = contentColor),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}