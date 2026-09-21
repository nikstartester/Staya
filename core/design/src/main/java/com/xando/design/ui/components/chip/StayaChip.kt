package com.xando.design.ui.components.chip

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.xando.design.ui.theme.extendedColors

/**
 * Цвета чипса по состояниям.
 *
 * @property containerColor Фон невыбранного чипса.
 * @property contentColor Цвет текста и иконок невыбранного чипса.
 * @property borderColor Обводка невыбранного чипса.
 * @property selectedContainerColor Фон выбранного чипса.
 * @property selectedContentColor Цвет текста и иконок выбранного чипса.
 * @property selectedBorderColor Обводка выбранного чипса.
 */
@Immutable
data class StayaChipColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val selectedContainerColor: Color = containerColor,
    val selectedContentColor: Color = contentColor,
    val selectedBorderColor: Color = borderColor,
) {

    /**@SelfDocumented*/
    fun containerColor(selected: Boolean): Color = if (selected) selectedContainerColor else containerColor

    /**@SelfDocumented*/
    fun contentColor(selected: Boolean): Color = if (selected) selectedContentColor else contentColor

    /**@SelfDocumented*/
    fun borderColor(selected: Boolean): Color = if (selected) selectedBorderColor else borderColor
}

/**
 * Объект, содержащий дефолтные значения для [StayaChip] и [StayaActionChip]
 */
object StayaChipDefaults {

    /**
     * Стандартная высота чипса
     */
    val Height = 36.dp

    /**
     * Горизонтальные отступы содержимого по умолчанию
     */
    val HorizontalPadding = 14.dp

    /**
     * Отступ между текстом и иконкой
     */
    val IconSpacing = 6.dp

    /**
     * Стиль текста чипса по умолчанию
     */
    val textStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.labelMedium

    /**
     * Прозрачность содержимого и обводки в выключенном состоянии
     */
    const val DisabledAlpha = 0.38f

    /**
     * Цвета чипса-переключателя: невыбранный - контур на прозрачном фоне, выбранный - закрашен.
     * Невыбранный фон - цвет выбранного с нулевой прозрачностью, а не [Color.Transparent]: иначе
     * анимация перехода шла бы через чёрный.
     */
    @Composable
    fun toggleColors(): StayaChipColors = with(MaterialTheme.extendedColors) {
        StayaChipColors(
            containerColor = secondaryBackgroundColor.copy(alpha = 0f),
            contentColor = textColor,
            borderColor = outlineColor,
            selectedContainerColor = secondaryBackgroundColor,
            selectedContentColor = secondaryBackgroundTextColor,
            selectedBorderColor = primaryColor,
        )
    }

    /**
     * Цвета чипса-действия: акцентные обводка и содержимое на прозрачном фоне.
     */
    @Composable
    fun actionColors(): StayaChipColors = with(MaterialTheme.extendedColors) {
        StayaChipColors(
            containerColor = Color.Transparent,
            contentColor = primaryTextColor,
            borderColor = primaryColor,
        )
    }
}

/**
 * Небольшая кнопка-переключатель с обводкой: выбранное состояние закрашено, невыбранное - только
 * контур. Используется как для множественного выбора тегов (интересы), так и для показа уже
 * выбранного тега с возможностью убрать его через [trailingIcon]. Смена состояния анимируется
 * плавным переходом цветов.
 *
 * @param text Текст чипса
 * @param selected Признак выбранного состояния
 * @param onClick Обработчик нажатия на весь чипс
 * @param modifier Модификатор для кастомизации компонента
 * @param enabled Определяет, активен ли чипс (например, при достижении лимита выбора)
 * @param trailingIcon Необязательная иконка в конце чипса (например, крестик для удаления)
 */
@Composable
fun StayaChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    BaseStayaChip(
        text = text,
        colors = StayaChipDefaults.toggleColors(),
        onClick = onClick,
        modifier = modifier,
        selected = selected,
        enabled = enabled,
        trailingIcon = trailingIcon,
    )
}

/**
 * Чипс-действие с акцентной обводкой: по нажатию не переключается, а запускает действие, например
 * открывает полный список.
 *
 * @param text Текст чипса
 * @param onClick Обработчик нажатия
 * @param modifier Модификатор для кастомизации компонента
 * @param enabled Определяет, активен ли чипс
 * @param leadingIcon Необязательная иконка в начале чипса (например, плюс)
 */
@Composable
fun StayaActionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    BaseStayaChip(
        text = text,
        colors = StayaChipDefaults.actionColors(),
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
    )
}

/**
 * Общая основа чипсов: кнопка-капсула фиксированной высоты с обводкой, текстом и необязательными
 * иконками по краям. Цвета берутся из [colors] по [selected] и анимируются при его смене; в
 * выключенном состоянии содержимое и обводка гаснут через [StayaChipDefaults.DisabledAlpha].
 */
@Composable
private fun BaseStayaChip(
    text: String,
    colors: StayaChipColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    val borderColor by animateColorAsState(colors.borderColor(selected))
    val containerColor by animateColorAsState(colors.containerColor(selected))
    val contentColor by animateColorAsState(colors.contentColor(selected))

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(StayaChipDefaults.Height),
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) borderColor else borderColor.copy(alpha = StayaChipDefaults.DisabledAlpha)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor.copy(alpha = StayaChipDefaults.DisabledAlpha),
        ),
        contentPadding = PaddingValues(horizontal = StayaChipDefaults.HorizontalPadding),
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(StayaChipDefaults.IconSpacing))
        }
        Text(text = text, style = StayaChipDefaults.textStyle)
        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(StayaChipDefaults.IconSpacing))
            trailingIcon()
        }
    }
}
