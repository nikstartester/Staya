package com.xando.navigation_api

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

/**
 * Интерфейс для регистрации navigation entries из feature модулей.
 *
 * Каждый feature модуль реализует EntryBuilder, который:
 * 1. Регистрируется через Hilt с помощью @IntoSet
 * 2. Автоматически собирается в app модуле
 * 3. Применяется в EntryProviderScope для создания NavDisplay
 *
 * Использование:
 * ```
 * @Provides
 * @IntoSet
 * fun provideAuthEntryBuilder(): EntryBuilder {
 *     return EntryBuilder { navigationController ->
 *         entry<LoginKey> { LoginScreen(...) }
 *         entry<SignUpKey> { SignUpScreen(...) }
 *     }
 * }
 * ```
 */
fun interface EntryBuilder {

    /**
     * Регистрирует navigation entries в EntryProviderScope.
     *
     * @param navigationController контроллер для выполнения навигации
     */
    fun EntryProviderScope<NavKey>.build(navigationController: NavigationController)
}