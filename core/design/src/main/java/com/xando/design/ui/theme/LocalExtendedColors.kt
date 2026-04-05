package com.xando.design.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        textColor = Color.Unspecified,

        iconColor = Color.Unspecified,

        primaryColor = Color.Unspecified,
        primaryTextColor = Color.Unspecified,
        primaryIconColor = Color.Unspecified,

        contrastTextColor = Color.Unspecified,
        contrastIconColor = Color.Unspecified,

        unaccentedTextColor = Color.Unspecified,
        unaccentedIconColor = Color.Unspecified,

        dangerColor = Color.Unspecified,

        successColor = Color.Unspecified,

        navigationBackgroundColor = Color.Unspecified,
        navigationSeparatorColor = Color.Unspecified,
        navigationActiveItemColor = Color.Unspecified,

        navigationTextColor = Color.Unspecified,
        navigationActiveTextColor = Color.Unspecified,

        navigationIconColor = Color.Unspecified,
        navigationActiveIconColor = Color.Unspecified,
    )
}