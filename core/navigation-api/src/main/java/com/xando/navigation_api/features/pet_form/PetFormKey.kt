package com.xando.navigation_api.features.pet_form

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Экран создания/редактирования питомца.
 *
 * @property petId Идентификатор питомца при редактировании; `null` при создании нового.
 */
@Serializable
data class PetFormKey(val petId: String? = null) : NavKey
