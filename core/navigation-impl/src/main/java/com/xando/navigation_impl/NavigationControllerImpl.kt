package com.xando.navigation_impl

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.xando.navigation_api.NavigationController

/**
 * Создаёт и запоминает [NavigationController], привязанный к [backStack].
 * Экземпляр пересоздаётся при изменении ссылки на [backStack].
 */
@Composable
fun rememberNavigationController(backStack: NavBackStack<NavKey>): NavigationController {
    return remember(backStack) { NavigationControllerImpl(backStack) }
}

/**
 * Реализация NavigationController.
 * Инкапсулирует всю логику работы с back stack.
 */
@Stable
class NavigationControllerImpl(private val backStack: NavBackStack<NavKey>) : NavigationController {

    override fun navigateTo(key: NavKey) {
        backStack.add(key)
    }

    override fun navigateBack(): Boolean =
        backStack.removeLastOrNull() != null

    override fun replaceWith(key: NavKey) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = key
        } else backStack.add(key)
    }

    override fun navigateAndClearStack(key: NavKey) {
        backStack.clear()
        backStack.add(key)
    }

    override fun navigateAndPopUpTo(
        destination: NavKey,
        popUpTo: NavKey,
        inclusive: Boolean
    ) {
        val index = backStack.indexOfLast { it == popUpTo }

        if (index != -1) {
            val removeFrom = if (inclusive) index else index + 1
            if (removeFrom < backStack.size) {
                backStack.subList(removeFrom, backStack.size).clear()
            }
        }

        backStack.add(destination)
    }

    override fun currentDestination(): NavKey? {
        return backStack.lastOrNull()
    }

    override fun canNavigateBack(): Boolean {
        return backStack.size > 1
    }
}