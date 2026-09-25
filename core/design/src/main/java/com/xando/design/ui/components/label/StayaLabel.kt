package com.xando.design.ui.components.label

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xando.design.ui.theme.StayaTheme
import com.xando.design.ui.theme.extendedColors

/**
 * Размер [StayaLabel]: задаёт отступы и стиль текста.
 */
enum class StayaLabelSize {

    /** Обычная метка. */
    LARGE,

    /** Компактная метка. */
    MEDIUM,
}

private val LargeContentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
private val MediumContentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)

/**
 * Неинтерактивная метка-капсула: текст в одну строку на цветном фоне.
 *
 * @param text Текст метки.
 * @param modifier Модификатор для кастомизации компонента.
 * @param size Размер метки.
 * @param containerColor Цвет фона.
 * @param contentColor Цвет текста.
 */
@Composable
fun StayaLabel(
    text: String,
    modifier: Modifier = Modifier,
    size: StayaLabelSize = StayaLabelSize.LARGE,
    containerColor: Color = MaterialTheme.extendedColors.secondaryBackgroundColor,
    contentColor: Color = MaterialTheme.extendedColors.secondaryBackgroundTextColor,
) {
    val contentPadding = when (size) {
        StayaLabelSize.LARGE -> LargeContentPadding
        StayaLabelSize.MEDIUM -> MediumContentPadding
    }
    val textStyle = when (size) {
        StayaLabelSize.LARGE -> MaterialTheme.typography.labelLarge
        StayaLabelSize.MEDIUM -> MaterialTheme.typography.labelMedium
    }

    Text(
        text = text,
        modifier = modifier
            .background(color = containerColor, shape = CircleShape)
            .padding(contentPadding),
        color = contentColor,
        style = textStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview(showBackground = true)
@Composable
private fun StayaLabelPreview() {
    StayaTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StayaLabel(text = "Игра в мяч")
                StayaLabel(text = "Прогулки в лесу")
            }
            with(MaterialTheme.extendedColors) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StayaLabel(
                        text = "Готовы общаться",
                        size = StayaLabelSize.MEDIUM,
                        containerColor = statusReadyBackgroundColor,
                        contentColor = statusReadyBackgroundTextColor,
                    )
                    StayaLabel(
                        text = "Течка",
                        size = StayaLabelSize.MEDIUM,
                        containerColor = statusHeatBackgroundColor,
                        contentColor = statusHeatBackgroundTextColor,
                    )
                    StayaLabel(
                        text = "Не подходить",
                        size = StayaLabelSize.MEDIUM,
                        containerColor = statusAvoidBackgroundColor,
                        contentColor = statusAvoidBackgroundTextColor,
                    )
                }
            }
            StayaLabel(
                text = "Очень длинное название интереса, которое не помещается",
                modifier = Modifier.width(160.dp),
            )
        }
    }
}
