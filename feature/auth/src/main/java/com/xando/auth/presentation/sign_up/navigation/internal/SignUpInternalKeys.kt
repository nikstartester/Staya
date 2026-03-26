package com.xando.auth.presentation.sign_up.navigation.internal

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Экран регистрации нового пользователя. Первое знакомство (ввод имени и фамилии).
 */
@Serializable
internal data object SignUpIntroductionKey : NavKey

/**
 * Экран регистрации нового пользователя. Немного о себе.
 */
@Serializable
internal data object SignUpAboutKey : NavKey

/**
 * Экран регистрации нового пользователя. Ввод логина.
 */
@Serializable
internal data object SignUpLoginKey : NavKey

/**
 * Экран регистрации нового пользователя. Ввод почты и пароля.
 */
@Serializable
internal data object SignUpEmailPasswordKey : NavKey