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

    primaryTextColor = LightColorScheme.primary,
    primaryIconColor = LightColorScheme.primary,

    contrastTextColor = Color(0xFFFFFFFF),
    contrastIconColor = Color(0xFFFFFFFF),

    unaccentedTextColor = Color(0xFF797979),
    unaccentedIconColor = Color(0xFF797979)
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