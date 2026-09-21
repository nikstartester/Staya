package com.xando.pet_interests_picker.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.interests_picker.InterestsPickerKey
import com.xando.pet_interests_picker.presentation.InterestsPickerScreen
import com.xando.pet_interests_picker.presentation.InterestsPickerViewModel

/**
 * Регистрирует navigation entries для модуля pet_interests_picker.
 *
 * @param navigationController Контроллер навигации приложения.
 */
internal fun EntryProviderScope<NavKey>.interestsPickerEntryBuilder(navigationController: NavigationController) {
    entry<InterestsPickerKey> { key ->
        InterestsPickerScreen(
            viewModel = hiltViewModel<InterestsPickerViewModel, InterestsPickerViewModel.Factory>(
                creationCallback = { factory -> factory.create(key.requestKey, key.currentInterests, key.maxSelection) }
            ),
            onBackClick = { navigationController.navigateBack() },
            onConfirm = { navigationController.navigateBack() }
        )
    }
}
