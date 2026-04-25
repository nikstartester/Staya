package com.xando.staya.presentation.home.bottom_navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xando.design.ui.theme.extendedColors

/**
 * Компонент ННП.
 */
@Composable
internal fun BottomNavigationBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    Column {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.extendedColors.navigationSeparatorColor)
        NavigationBar(
            containerColor = MaterialTheme.extendedColors.navigationBackgroundColor,
            tonalElevation = 0.dp
        ) {
            BottomTab.entries.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(tab.iconRes),
                            contentDescription = stringResource(tab.labelRes)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(tab.labelRes),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    },
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    colors = navigationBarItemColors()
                )
            }
        }
    }
}

@Composable
private fun navigationBarItemColors() =
    NavigationBarItemColors(
        selectedIconColor = MaterialTheme.extendedColors.navigationActiveIconColor,
        selectedTextColor = MaterialTheme.extendedColors.navigationActiveTextColor,
        selectedIndicatorColor = MaterialTheme.extendedColors.navigationActiveItemColor,
        unselectedIconColor = MaterialTheme.extendedColors.navigationIconColor,
        unselectedTextColor = MaterialTheme.extendedColors.navigationTextColor,
        disabledIconColor = MaterialTheme.extendedColors.unaccentedIconColor,
        disabledTextColor = MaterialTheme.extendedColors.unaccentedTextColor
    )