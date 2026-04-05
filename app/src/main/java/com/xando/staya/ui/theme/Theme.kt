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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF466677),
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFFFFFFF),
    // Дублируется в colors.xml (app_surface_color) для splash screen
    surface = Color(0xFFEFF5FC)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

private val LightExtendedColors = ExtendedColors(
    textColor = Color(0xFF000000),

    iconColor = Color(0xFF333333),

    // TODO: Вот от этой бодяги бы избавиться
    primaryColor = LightColorScheme.primary,
    primaryTextColor = LightColorScheme.primary,
    primaryIconColor = LightColorScheme.primary,

    contrastTextColor = Color(0xFFFFFFFF),
    contrastIconColor = Color(0xFFFFFFFF),

    unaccentedTextColor = Color(0xFF797979),
    unaccentedIconColor = Color(0xFF797979),


    dangerColor = Color(0xFFCF4B42),

    successColor = Color(0xFF3A9A5C),

    navigationBackgroundColor = Color(0xFFFFFFFF),
    navigationSeparatorColor = Color(0xFFF2F2F2),
    navigationActiveItemColor = Color(0xFFEFF5FC),

    navigationTextColor = Color(0xFF333333),
    navigationActiveTextColor = Color(0xFF000000),

    navigationIconColor = Color(0xFF333333),
    navigationActiveIconColor = Color(0xFF000000),
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