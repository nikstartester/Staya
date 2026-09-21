package com.xando.navigation_api.features.interests_picker

import androidx.navigation3.runtime.NavKey
import com.xando.core.models.pet.PetInterest
import kotlinx.serialization.Serializable

/**
 * Экран выбора интересов питомца.
 *
 * Результат (выбранные интересы) возвращается вызывающему экрану через [com.xando.navigation_api.NavigationResultStore] по [requestKey].
 *
 * @property requestKey Ключ, под которым вызывающий экран ожидает результат.
 * @property currentInterests Текущий выбор интересов для подсветки в списке.
 * @property maxSelection Максимальное количество интересов, которое можно выбрать одновременно.
 */
@Serializable
data class InterestsPickerKey(
    val requestKey: String,
    val currentInterests: List<PetInterest> = emptyList(),
    val maxSelection: Int = Int.MAX_VALUE,
) : NavKey
