package com.xando.staya.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xando.design.animations.noTransition
import com.xando.design.animations.predictiveBackTransition
import com.xando.design.animations.rightInTransition
import com.xando.design.animations.rightOutTransition
import com.xando.design.ui.snackbar.LocalSnackbarController
import com.xando.design.ui.snackbar.StayaSnackbarHost
import com.xando.design.ui.snackbar.rememberStayaSnackbarController
import com.xando.navigation_api.EntryBuilder
import com.xando.navigation_api.features.auth.LoginKey
import com.xando.navigation_api.features.home.HomeKey
import com.xando.navigation_impl.rememberNavigationController
import com.xando.staya.presentation.home.HomeScreen

/**
 * Корневой хост навигации приложения.
 * Определяет стартовый экран (Home или Login) в зависимости от состояния авторизации,
 * управляет root-level back stack и делегирует построение навигационных записей
 * зарегистрированным [EntryBuilder]-ам.
 */
@Composable
internal fun RootHost(entryBuilders: Set<EntryBuilder>, modifier: Modifier = Modifier, viewModel: RootViewModel) {
    val isAuthorized by viewModel.isAuthorized.collectAsStateWithLifecycle()

    // Держим сплеш пока не будет информации о логине. Это дополнительная проверка.
    if (isAuthorized == null) return

    val backStack = rememberNavBackStack(if (isAuthorized == true) HomeKey else LoginKey)
    val navigationController = rememberNavigationController(backStack)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is RootEvent.AuthStateChanged -> {
                        if (event.isAuthorized) {
                            navigationController.navigateAndClearStack(HomeKey)
                        } else {
                            navigationController.navigateAndClearStack(LoginKey)
                        }
                    }
                }
            }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = rememberStayaSnackbarController(snackbarHostState)

    AppCloseBackHandler()

    CompositionLocalProvider(LocalSnackbarController provides snackbarController) {
        Box(modifier = modifier) {
            NavDisplay(
                backStack = backStack,
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
                            HomeScreen(entryBuilders, parentNavigationController = navigationController)
                        }

                        entryBuilders.forEach { builder ->
                            with(builder) {
                                build(navigationController)
                            }
                        }
                    })

            StayaSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .safeDrawingPadding()
            )
        }
    }
}
