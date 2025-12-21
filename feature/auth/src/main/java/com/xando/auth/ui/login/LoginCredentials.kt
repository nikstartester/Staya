package com.xando.auth.ui.login

/**
 * Данные, вводимые пользователем для авторизации.
 *
 * @property email Email или логин пользователя.
 * @property password Пароль пользователя.
 */
internal data class LoginCredentials(
    val email: String = "",
    val password: String = ""
)