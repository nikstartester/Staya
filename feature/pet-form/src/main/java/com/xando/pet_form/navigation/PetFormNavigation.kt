package com.xando.pet_form.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.breed_picker.BreedPickerKey
import com.xando.navigation_api.features.interests_picker.InterestsPickerKey
import com.xando.navigation_api.features.pet_form.PetFormKey
import com.xando.pet_form.presentation.PetFormScreen
import com.xando.pet_form.presentation.PetFormViewModel

/**
 * Регистрирует navigation entries для модуля pet_form.
 *
 * @param navigationController Контроллер навигации приложения.
 */
internal fun EntryProviderScope<NavKey>.petFormEntryBuilder(navigationController: NavigationController) {
    entry<PetFormKey> { key ->
        PetFormScreen(
            viewModel = hiltViewModel<PetFormViewModel, PetFormViewModel.Factory>(
                creationCallback = { factory -> factory.create(key.petId) }
            ),
            onBackClick = { navigationController.navigateBack() },
            onPickBreed = { currentBreed ->
                navigationController.navigateTo(BreedPickerKey(BREED_PICKER_KEY, currentBreed))
            },
            onPickInterests = { currentInterests, maxSelection ->
                navigationController.navigateTo(
                    InterestsPickerKey(INTERESTS_PICKER_KEY, currentInterests, maxSelection)
                )
            }
        )
    }
}

/**
 * Ключ для получения результата выбора породы.
 */
internal const val BREED_PICKER_KEY = "PET_FORM_BREED_PICKER_KEY"

/**
 * Ключ для получения результата выбора интересов.
 */
internal const val INTERESTS_PICKER_KEY = "PET_FORM_INTERESTS_PICKER_KEY"
