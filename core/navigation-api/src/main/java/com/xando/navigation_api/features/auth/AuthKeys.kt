package com.xando.navigation_api.features.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Маркер-объект, определяющий вложенный граф навигации для процесса авторизации
 */
@Serializable
data object LoginKey : NavKey