package com.xando.pet_breed_picker.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.breed_picker.BreedPickerKey
import com.xando.pet_breed_picker.presentation.BreedPickerScreen
import com.xando.pet_breed_picker.presentation.BreedPickerViewModel

/**
 * Регистрирует navigation entries для модуля pet_breed_picker.
 *
 * @param navigationController Контроллер навигации приложения.
 */
internal fun EntryProviderScope<NavKey>.breedPickerEntryBuilder(navigationController: NavigationController) {
    entry<BreedPickerKey> { key ->
        BreedPickerScreen(
            viewModel = hiltViewModel<BreedPickerViewModel, BreedPickerViewModel.Factory>(
                creationCallback = { factory -> factory.create(key.requestKey, key.currentBreed) }
            ),
            onBackClick = { navigationController.navigateBack() },
            onConfirm = { navigationController.navigateBack() }
        )
    }
}
