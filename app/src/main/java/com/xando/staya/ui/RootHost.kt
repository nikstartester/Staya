package com.xando.staya.ui

import android.preference.PreferenceManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xando.design.animations.predictiveBackTransition
import com.xando.design.animations.noTransition
import com.xando.design.animations.rightInTransition
import com.xando.design.animations.rightOutTransition
import com.xando.navigation_api.EntryBuilder
import com.xando.navigation_api.features.auth.LoginKey
import com.xando.navigation_api.features.home.HomeKey
import com.xando.staya.navigation_impl.NavigationControllerImpl
import com.xando.staya.ui.bottom_navigation_container.BottomNavContainer

@Composable
fun RootHost(
    entryBuilders: Set<EntryBuilder>,
    modifier: Modifier = Modifier,
) {
    val initialKey = getStartDestination()

    val backStack = rememberNavBackStack(initialKey)

    val navigationController = remember(backStack) {
        NavigationControllerImpl(backStack)
    }

    AppCloseBackHandler()

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = {
            navigationController.navigateBack()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = { rightInTransition() },
        popTransitionSpec = { rightOutTransition() },
        predictivePopTransitionSpec = {
            predictiveBackTransition()
        },
        entryProvider =
            entryProvider {
                entry<HomeKey>(
                    metadata = NavDisplay.transitionSpec { noTransition }
                            + NavDisplay.popTransitionSpec { noTransition }
                            + NavDisplay.predictivePopTransitionSpec { noTransition }
                ) {
                    BottomNavContainer(
                        parentNavigationController = navigationController
                    )
                }

                entryBuilders.forEach { builder ->
                    with(builder) {
                        build(navigationController)
                    }
                }
            })
}

@Composable
private fun getStartDestination(): NavKey = if (isLogin()) HomeKey else LoginKey

@Composable
private fun isLogin() =
    PreferenceManager.getDefaultSharedPreferences(LocalContext.current).getBoolean("IS_LOGIN", false)