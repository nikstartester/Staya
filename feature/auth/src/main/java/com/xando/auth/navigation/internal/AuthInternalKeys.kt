package com.xando.auth.navigation.internal

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Экран регистрации нового пользователя
 */
@Serializable
data object SignUpKey : NavKey

/**
 * Экран восстановления пароля
 */
@Serializable
data object ForgotPasswordKey : NavKey