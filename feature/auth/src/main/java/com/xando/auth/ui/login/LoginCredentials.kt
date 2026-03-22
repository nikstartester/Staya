package com.xando.auth.ui.login

/**
 * Данные, вводимые пользователем для авторизации.
 *
 * @property login Email или логин пользователя.
 * @property password Пароль пользователя.
 */
internal data class LoginCredentials(
    val login: String = "",
    val password: String = ""
)