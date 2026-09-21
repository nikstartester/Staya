package com.xando.pet_form.presentation.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale

/** Количество цифр в полностью введённой дате рождения: ДДММГГГГ. */
internal const val BIRTH_DATE_DIGITS = 8

/**
 * Формат содержимого поля даты рождения: цифры без разделителей, точки дорисовывает
 * [DateVisualTransformation][com.xando.design.ui.components.text_field.DateVisualTransformation].
 * Разбор строгий, чтобы «31.02.2022» не превращалось в конец февраля.
 */
private val BirthDateInputFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("ddMMuuuu", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT)

/** Самая поздняя допустимая дата рождения: питомец не может родиться позже сегодняшнего дня. */
internal fun latestBirthDate(): LocalDate = LocalDate.now()

/** Содержимое поля даты рождения как дата; `null`, если цифр не хватает или такой даты не существует. */
internal fun String.toBirthDateOrNull(): LocalDate? {
    if (length != BIRTH_DATE_DIGITS) return null

    return runCatching { LocalDate.parse(this, BirthDateInputFormatter) }.getOrNull()
}

/** Дата как содержимое поля даты рождения. */
internal fun LocalDate.toBirthDateInput(): String = format(BirthDateInputFormatter)

/**
 * Дата как миллисекунды эпохи начала этого дня в UTC: в таком виде хранит выбор
 * [DatePicker][androidx.compose.material3.DatePicker].
 */
internal fun LocalDate.toUtcEpochMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

/**
 * Выбор [DatePicker][androidx.compose.material3.DatePicker] как дата. Миллисекунды читаются в UTC,
 * иначе в отрицательных часовых поясах выбранный день превратился бы в предыдущий.
 */
internal fun Long.utcEpochMillisToLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()
