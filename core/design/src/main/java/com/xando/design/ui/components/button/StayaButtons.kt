package com.xando.design.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.xando.design.ui.theme.extendedColors
import com.xando.design.utils.throttledClick

/**
 * Объект, содержащий дефолтные значения для кнопок
 */
object StayaButtonDefaults {

    /**
     * Стандартная высота кнопок
     */
    val Height = 48.dp

    /**
     * Размер индикатора загрузки в кнопке
     */
    val LoadingIndicatorSize = 24.dp

    /**
     * Ширина границы для [StayaOutlinedButton]
     */
    val OutlinedBorderWidth = 1.5.dp

    /**
     * Стиль текста кнопок по умолчанию
     */
    val textStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography.titleSmall

    /**
     * Цвет текста для обычной кнопки (контрастный с фоном)
     */
    val buttonTextColor: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.extendedColors.contrastTextColor

    /**
     * Цвет текста для outlined кнопки
     */
    val outlinedButtonTextColor: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.extendedColors.primaryTextColor

    /**
     * Цвет индикатора загрузки в кнопке
     */
    val loadingIndicatorColor: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onPrimary

    /**
     * Цвет границы для outlined кнопки
     */
    val outlinedBorderColor: Color
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.primary

    /**
     * Создает [BorderStroke] для outlined кнопки с дефолтными значениями
     */
    @Composable
    fun outlinedBorder(): BorderStroke = BorderStroke(
        width = OutlinedBorderWidth,
        color = outlinedBorderColor
    )
}

/**
 * Стандартизированная кнопка
 *
 * @param text Текст кнопки
 * @param onClick Обработчик нажатия на кнопку. Защищён от двойного нажатия.
 * @param modifier Модификатор для кастомизации компонента
 * @param isLoading Флаг состояния загрузки. При `true` отображается индикатор загрузки вместо текста и кнопка становится неактивной
 * @param enabled Определяет, активна ли кнопка
 */
@Composable
fun StayaButton(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = throttledClick(onClick = onClick),
        modifier = modifier.height(StayaButtonDefaults.Height),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            ButtonProgressIndicator()
        } else {
            Text(
                text = text,
                style = StayaButtonDefaults.textStyle,
                color = StayaButtonDefaults.buttonTextColor
            )
        }
    }
}

/**
 * Стандартизированная кнопка с обводкой
 *
 * @param text Текст кнопки
 * @param onClick Обработчик нажатия на кнопку
 * @param modifier Модификатор для кастомизации компонента
 * @param isLoading Флаг состояния загрузки. При `true` отображается индикатор загрузки вместо текста и кнопка становится неактивной
 * @param enabled Определяет, активна ли кнопка
 */
@Composable
fun StayaOutlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = throttledClick(onClick = onClick),
        modifier = modifier.height(StayaButtonDefaults.Height),
        border = StayaButtonDefaults.outlinedBorder(),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            ButtonProgressIndicator()
        } else {
            Text(
                text = text,
                color = StayaButtonDefaults.outlinedButtonTextColor,
                style = StayaButtonDefaults.textStyle
            )
        }
    }
}

@Composable
private fun ButtonProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(StayaButtonDefaults.LoadingIndicatorSize),
        color = StayaButtonDefaults.loadingIndicatorColor
    )
}