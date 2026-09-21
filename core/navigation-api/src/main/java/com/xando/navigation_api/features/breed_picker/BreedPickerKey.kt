package com.xando.navigation_api.features.breed_picker

import androidx.navigation3.runtime.NavKey
import com.xando.core.models.pet.PetBreed
import kotlinx.serialization.Serializable

/**
 * Экран выбора породы питомца.
 *
 * Результат (выбранная порода) возвращается вызывающему экрану через [com.xando.navigation_api.NavigationResultStore] по [requestKey].
 *
 * @property requestKey Ключ, под которым вызывающий экран ожидает результат.
 * @property currentBreed Текущее значение породы для подсветки в списке.
 */
@Serializable
data class BreedPickerKey(val requestKey: String, val currentBreed: PetBreed? = null) : NavKey
