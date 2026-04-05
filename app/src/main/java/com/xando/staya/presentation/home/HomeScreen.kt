package com.xando.staya.presentation.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xando.navigation_api.EntryBuilder
import com.xando.navigation_api.NavigationController
import com.xando.navigation_impl.rememberNavigationController
import com.xando.staya.presentation.AppCloseBackHandler
import com.xando.staya.presentation.home.bottom_navigation.BottomNavigationBar
import com.xando.staya.presentation.home.bottom_navigation.BottomTab
import kotlinx.serialization.Serializable

/**
 * Главный экран приложения. Контейнер с bottom navigation bar.
 * Имеет отдельный NavDisplay с собственными декораторами.
 *
 * @param entryBuilders общий набор экранов приложения, регистрируемых в навигационном графе
 * HomeScreen. Сам экран определяет какие из них отображать во вкладках bottom navigation.
 * @param parentNavigationController контроллер для навигации из вкладок.
 */
@Composable
internal fun HomeScreen(entryBuilders: Set<EntryBuilder>, parentNavigationController: NavigationController) {
    val bottomBackStack = rememberNavBackStack(MapKey)
    val bottomNavigationController = rememberNavigationController(bottomBackStack)

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
                        BottomTab.MESSAGES -> ProfileKey
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

                entryBuilders.forEach { builder ->
                    with(builder) {
                        build(parentNavigationController)
                    }
                }
            }
        )
    }
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
    TestScreen(stringResource(BottomTab.MAP.labelRes))
}

@Composable
private fun MyPetsTabScreen() {
    TestScreen(stringResource(BottomTab.PETS.labelRes))
}

@Composable
private fun ProfileTabScreen() {
    TestScreen(stringResource(BottomTab.PROFILE.labelRes))
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
