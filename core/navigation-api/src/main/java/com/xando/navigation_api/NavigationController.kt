package com.xando.navigation_api

import androidx.compose.runtime.Stable
import androidx.navigation3.runtime.NavKey

/**
 * Интерфейс для управления навигацией приложения.
 * Предоставляет методы для модификации back stack.
 */
@Stable
interface NavigationController {

    /**
     * Навигация вперед - добавляет новый экран в стек
     */
    fun navigateTo(key: NavKey)

    /**
     * Возврат назад - удаляет последний экран из стека
     * @return true если навигация выполнена, false если стек пустой
     */
    fun navigateBack(): Boolean

    /**
     * Замена текущего экрана на новый
     */
    fun replaceWith(key: NavKey)

    /**
     * Очистка всего стека и переход на новый экран
     */
    fun navigateAndClearStack(key: NavKey)

    /**
     * Навигация с очисткой стека до определенного ключа
     * @param destination новый экран назначения
     * @param popUpTo ключ, до которого очищаем стек
     * @param inclusive включать ли popUpTo ключ в очистку
     */
    fun navigateAndPopUpTo(
        destination: NavKey,
        popUpTo: NavKey,
        inclusive: Boolean = false
    )

    /**
     * Получить текущий экран (последний в стеке)
     */
    fun currentDestination(): NavKey?

    /**
     * Проверка возможности возврата назад
     */
    fun canNavigateBack(): Boolean
}