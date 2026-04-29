package com.xando.navigation_api.features.pets_list

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Маркер-объект, определяющий вложенный граф навигации для списка питомцев.
 */
@Serializable
data object PetListKey : NavKey
