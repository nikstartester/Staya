package com.xando.staya.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xando.design.animations.noTransition
import com.xando.design.animations.predictiveBackTransition
import com.xando.design.animations.rightInTransition
import com.xando.design.animations.rightOutTransition
import com.xando.navigation_api.EntryBuilder
import com.xando.navigation_api.features.auth.LoginKey
import com.xando.navigation_api.features.home.HomeKey
import com.xando.navigation_impl.rememberNavigationController
import com.xando.staya.presentation.bottom_navigation_container.BottomNavContainer

/**
 * Корневой хост навигации приложения.
 * Определяет стартовый экран (Home или Login) в зависимости от состояния авторизации,
 * управляет root-level back stack и делегирует построение навигационных записей
 * зарегистрированным [EntryBuilder]-ам.
 */
@Composable
fun RootHost(
    entryBuilders: Set<EntryBuilder>,
    modifier: Modifier = Modifier,
    viewModel: RootViewModel = hiltViewModel(),
) {
    val isAuthorized by viewModel.isAuthorized.collectAsStateWithLifecycle()

    // TODO: Будем задерживать splash screen пока нет данных авторизации.
    //  Пока просто пустой экран - загрузка с диска быстро проходит.
    if (isAuthorized == null) return

    val backStack = rememberNavBackStack(if (isAuthorized == true) HomeKey else LoginKey)
    val navigationController = rememberNavigationController(backStack)

    LaunchedEffect(isAuthorized) {
        when (isAuthorized) {
            true -> if (backStack.last() != HomeKey) {
                navigationController.navigateAndClearStack(HomeKey)
            }

            false -> if (backStack.last() != LoginKey) {
                navigationController.navigateAndClearStack(LoginKey)
            }

            else -> Unit
        }
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