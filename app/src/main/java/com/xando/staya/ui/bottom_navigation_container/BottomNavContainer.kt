package com.xando.staya.ui.bottom_navigation_container


import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xando.navigation_api.NavigationController
import com.xando.staya.navigation_impl.NavigationControllerImpl
import com.xando.staya.ui.AppCloseBackHandler
import kotlinx.serialization.Serializable
import com.xando.core.design.R as RDesign

/**
 * Контейнер с bottom navigation bar.
 * Имеет отдельный NavDisplay с собственными декораторами.
 */
@Composable
internal fun BottomNavContainer(parentNavigationController: NavigationController) {
    val bottomBackStack = rememberNavBackStack(MapKey)

    val bottomNavigationController = remember(bottomBackStack) {
        NavigationControllerImpl(bottomBackStack)
    }

    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.MAP) }

    AppCloseBackHandler()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    val targetKey = when (tab) {
                        BottomTab.MAP -> MapKey
                        BottomTab.PETS -> MyPetsKey
                        BottomTab.PROFILE -> ProfileKey
                    }
                    bottomNavigationController.navigateAndClearStack(targetKey)
                }
            )
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = bottomBackStack,
            modifier = Modifier.padding(paddingValues),
            onBack = {
                bottomNavigationController.navigateBack()
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),

            entryProvider = entryProvider {
                entry<MapKey> { MapTabScreen() }
                entry<MyPetsKey> { MyPetsTabScreen() }
                entry<ProfileKey> { ProfileTabScreen() }
            }
        )
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        BottomTab.entries.forEach { tab ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(RDesign.drawable.arrow_back_24dp),
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

private enum class BottomTab(
    val label: String,
    @DrawableRes
    val iconRes: Int
) {
    MAP("Карта", -1),
    PETS("Питомцы", -1),
    PROFILE("Профиль", -1)
}

//region Удалить после реализации
@Serializable
private object MapKey : NavKey

@Serializable
private object MyPetsKey : NavKey

@Serializable
private object ProfileKey : NavKey

@Composable
private fun MapTabScreen() {
    TestScreen(BottomTab.MAP.label)
}

@Composable
private fun MyPetsTabScreen() {
    TestScreen(BottomTab.PETS.label)
}

@Composable
private fun ProfileTabScreen() {
    TestScreen(BottomTab.PROFILE.label)
}

@Composable
fun TestScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}
//endregion
