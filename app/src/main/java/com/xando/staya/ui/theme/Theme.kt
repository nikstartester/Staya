package com.xando.staya.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.xando.design.ui.theme.ExtendedColors
import com.xando.design.ui.theme.LocalExtendedColors

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF466677),
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightExtendedColors = ExtendedColors(
    backgroundColor = Color(0xFFFFFFFF),

    outlineColor = Color(0xFF747C81),

    textColor = Color(0xFF000000),
    placeholderTextColor = Color(0xFF8DA3BD),

    iconColor = Color(0xFF333333),
    placeholderIconColor = Color(0xFFBECEE0),

    // TODO: Вот от этой бодяги бы избавиться
    primaryColor = Color(0xFF466677),
    primaryTextColor = Color(0xFF466677),
    primaryIconColor = Color(0xFF466677),
    primaryBackgroundColor = Color(0xFFC6E7FB),
    primaryBackgroundTextColor = Color(0xFF004D66),
    // Используется как Surface. Дублируется в colors.xml (app_surface_color) для splash screen. При изменении менять в двух местах!
    primaryUnaccentedBackgroundColor = Color(0xFFEFF5FC),

    secondaryColor = Color(0xFF4E626D),
    secondaryBackgroundColor = Color(0xFFD0E6F3),
    secondaryBackgroundTextColor = Color(0xFF41545F),

    contrastTextColor = Color(0xFFFFFFFF),
    contrastIconColor = Color(0xFFFFFFFF),

    unaccentedTextColor = Color(0xFF797979),
    unaccentedIconColor = Color(0xFF797979),
    unaccentedBackgroundColor = Color(0xFFF8F8F8),

    dangerColor = Color(0xFFCF4B42),

    successColor = Color(0xFF3A9A5C),

    navigationBackgroundColor = Color(0xFFFFFFFF),
    navigationSeparatorColor = Color(0xFFF2F2F2),
    navigationActiveItemColor = Color(0xFFD0E6F3),

    navigationTextColor = Color(0xFF333333),
    navigationActiveTextColor = Color(0xFF000000),

    navigationIconColor = Color(0xFF333333),
    navigationActiveIconColor = Color(0xFF41545F),
)

private val LightColorScheme = lightColorScheme(
    // Primary
    primary = LightExtendedColors.primaryColor,
    onPrimary = LightExtendedColors.contrastTextColor,
    primaryContainer = LightExtendedColors.primaryBackgroundColor,
    onPrimaryContainer = LightExtendedColors.primaryBackgroundTextColor,

    // Secondary
    secondary = LightExtendedColors.secondaryColor,
    onSecondary = LightExtendedColors.textColor,
    secondaryContainer = LightExtendedColors.secondaryBackgroundColor,
    onSecondaryContainer = LightExtendedColors.secondaryBackgroundTextColor,

    // Error / Danger
    error = LightExtendedColors.dangerColor,
    onError = LightExtendedColors.contrastTextColor,
    errorContainer = LightExtendedColors.dangerColor.copy(alpha = 0.20f),
    onErrorContainer = LightExtendedColors.dangerColor,

    // Background & Surface
    background = LightExtendedColors.backgroundColor,
    onBackground = LightExtendedColors.textColor,

    // Surface дублируется в colors.xml (app_surface_color) для splash screen. При изменении менять в двух местах!
    surface = LightExtendedColors.primaryUnaccentedBackgroundColor,
    onSurface = LightExtendedColors.textColor,
    surfaceVariant = LightExtendedColors.primaryUnaccentedBackgroundColor,
    onSurfaceVariant = LightExtendedColors.textColor,

    // Surface Containers (tonal elevation)
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFFFFFFF),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFFFFFFF),

    // Outlines & Dividers
    outline = LightExtendedColors.outlineColor,
    outlineVariant = LightExtendedColors.primaryColor,

    // Inverse (Snackbar, Dialog, BottomSheet)
    /*inverseSurface = LightExtendedColors.textColor,
    inverseOnSurface = LightExtendedColors.contrastTextColor,
    inversePrimary = LightExtendedColors.primaryColor,*/

    // System
    scrim = Color(0xFF000000),
    surfaceTint = Color.Unspecified
)

// Пока без темной темы
private val DarkExtendedColors = LightExtendedColors


@Composable
fun StayaTheme(
    darkTheme: Boolean = false, //isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}