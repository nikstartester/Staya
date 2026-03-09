package com.xando.auth.navigation.internal

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Экран регистрации нового пользователя
 */
@Serializable
internal data object SignUpKey : NavKey

/**
 * Экран восстановления пароля
 */
@Serializable
internal data object ForgotPasswordKey : NavKey