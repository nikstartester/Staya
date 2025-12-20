package com.xando.design.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    // Цвета текста
    val textColor: Color,

    // Цвета иконок
    val iconColor: Color,

    // Смысловые цвета
    val primaryTextColor: Color,
    val primaryIconColor: Color,

    val contrastTextColor: Color,
    val contrastIconColor: Color,

    val unaccentedTextColor: Color,
    val unaccentedIconColor: Color
)

val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current
