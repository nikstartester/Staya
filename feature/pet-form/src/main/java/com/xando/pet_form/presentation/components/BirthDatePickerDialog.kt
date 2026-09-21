package com.xando.pet_form.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.xando.pet_form.presentation.utils.latestBirthDate
import com.xando.pet_form.presentation.utils.toUtcEpochMillis
import java.time.LocalDate

/**
 * Календарь выбора даты рождения. Дни и годы позже [latestBirthDate] недоступны для выбора.
 *
 * @param initialMillis Изначально выбранная дата в миллисекундах эпохи (UTC); `null`, если ничего не выбрано.
 * @param onConfirm Обработчик подтверждения выбранной даты в миллисекундах эпохи (UTC).
 * @param onDismiss Обработчик закрытия календаря без выбора.
 */
@Composable
internal fun BirthDatePickerDialog(
    initialMillis: Long?,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectableDates = remember { NotAfterSelectableDates(latestDate = latestBirthDate()) }
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = selectableDates
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let(onConfirm) },
                enabled = state.selectedDateMillis != null
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    ) {
        DatePicker(state = state)
    }
}

/** Даты календаря не позже [latestDate]: более поздние дни и годы недоступны для выбора. */
private class NotAfterSelectableDates(private val latestDate: LocalDate) : SelectableDates {
    private val latestMillis = latestDate.toUtcEpochMillis()

    override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= latestMillis
    override fun isSelectableYear(year: Int): Boolean = year <= latestDate.year
}