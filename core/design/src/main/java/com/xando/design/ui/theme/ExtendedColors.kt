package com.xando.design.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    // Цвета фона
    val backgroundColor: Color,

    // Цвета обводки
    val outlineColor: Color,

    // Цвета текста
    val textColor: Color,
    val placeholderTextColor: Color,

    // Цвета иконок
    val iconColor: Color,
    val placeholderIconColor: Color,

    // Смысловые цвета
    val primaryColor: Color,
    val primaryTextColor: Color,
    val primaryIconColor: Color,
    val primaryBackgroundColor: Color,
    val primaryBackgroundTextColor: Color,
    val primaryUnaccentedBackgroundColor: Color,

    val secondaryColor: Color,
    val secondaryBackgroundColor: Color,
    val secondaryBackgroundTextColor: Color,

    val contrastTextColor: Color,
    val contrastIconColor: Color,

    val unaccentedTextColor: Color,
    val unaccentedIconColor: Color,
    val unaccentedBackgroundColor: Color,

    val dangerColor: Color,

    val successColor: Color,

    // Навигация
    val navigationBackgroundColor: Color,
    val navigationSeparatorColor: Color,
    val navigationActiveItemColor: Color,

    val navigationTextColor: Color,
    val navigationActiveTextColor: Color,

    val navigationIconColor: Color,
    val navigationActiveIconColor: Color
)

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
