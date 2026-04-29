package com.xando.pets_list.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.pets_list.PetListKey
import com.xando.pets_list.presentation.PetListScreen

/**
 * Регистрирует navigation entries для модуля pets_list.
 *
 * @param navigationController Контроллер навигации приложения.
 */
internal fun EntryProviderScope<NavKey>.petsListEntryBuilder(navigationController: NavigationController) {
    entry<PetListKey> {
        PetListScreen(
            viewModel = hiltViewModel()
        )
    }
}
