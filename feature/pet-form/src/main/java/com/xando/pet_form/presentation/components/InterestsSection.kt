package com.xando.pet_form.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xando.core.models.pet.PetInterest
import com.xando.core.pet_dictionary.labelRes
import com.xando.design.ui.components.chip.StayaActionChip
import com.xando.design.ui.components.chip.StayaChip
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.pet_form.R
import com.xando.core.design.R as RDesign

/**
 * Секция интересов: заголовок со счётчиком выбранного, чипы-переключатели и чип перехода к полному
 * списку. Когда выбрано [maxInterests], невыбранные чипы гаснут.
 *
 * @param interests Интересы, показанные чипами, в порядке показа.
 * @param chosenInterests Выбранные интересы.
 * @param maxInterests Максимальное количество интересов, которое можно выбрать.
 * @param onToggle Переключает выбор интереса.
 * @param onAddClick Обработчик нажатия на чип перехода к полному списку.
 */
@Composable
internal fun InterestsSection(
    interests: List<PetInterest>,
    chosenInterests: List<PetInterest>,
    maxInterests: Int,
    onToggle: (PetInterest) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLimitReached = chosenInterests.size >= maxInterests

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            FormSectionTitle(text = stringResource(R.string.pet_form_interests_label))
            Text(
                text = stringResource(R.string.pet_form_interests_count, chosenInterests.size, maxInterests),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.extendedColors.unaccentedTextColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            interests.forEach { interest ->
                val isSelected = interest in chosenInterests
                StayaChip(
                    text = stringResource(interest.labelRes),
                    selected = isSelected,
                    enabled = isSelected || !isLimitReached,
                    onClick = { onToggle(interest) }
                )
            }
            StayaActionChip(
                text = stringResource(R.string.pet_form_interests_more),
                onClick = onAddClick,
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(RDesign.drawable.design_ic_add_24px),
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.primaryIconColor
                    )
                }
            )
        }
    }
}