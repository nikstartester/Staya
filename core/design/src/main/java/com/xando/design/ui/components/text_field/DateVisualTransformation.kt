package com.xando.design.ui.components.text_field

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Показывает дату, введённую цифрами без разделителей, в виде `ДД.ММ.ГГГГ`. Точка появляется сразу,
 * как только заполнены день или месяц: «12» → «12.», «1209» → «12.09.», «12092022» → «12.09.2022».
 *
 * Значение поля при этом остаётся только цифрами, поэтому курсор и удаление работают по цифрам, а
 * точки пропускаются сами: Backspace после «12.» стирает двойку, а не точку. Цифры сверх восьми
 * показываются как есть, без разделителей.
 */
object DateVisualTransformation : VisualTransformation {

    private const val SEPARATOR = '.'

    /** Сколько цифр должно быть введено перед каждым разделителем: две цифры дня и ещё две месяца. */
    private val digitsBeforeSeparators = listOf(2, 4)

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val formatted = buildString {
            digits.forEachIndexed { index, digit ->
                if (index in digitsBeforeSeparators) append(SEPARATOR)
                append(digit)
            }
            if (digits.length in digitsBeforeSeparators) append(SEPARATOR)
        }
        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = DateOffsetMapping(originalLength = digits.length, transformedLength = formatted.length)
        )
    }

    private class DateOffsetMapping(
        private val originalLength: Int,
        private val transformedLength: Int,
    ) : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            val separatorsBefore = digitsBeforeSeparators.count { digits -> digits <= offset }
            return (offset + separatorsBefore).coerceIn(0, transformedLength)
        }

        override fun transformedToOriginal(offset: Int): Int {
            // Разделитель с номером index стоит в отформатированном тексте на позиции digits + index.
            val separatorsBefore =
                digitsBeforeSeparators.withIndex().count { (index, digits) -> digits + index < offset }
            return (offset - separatorsBefore).coerceIn(0, originalLength)
        }
    }
}
