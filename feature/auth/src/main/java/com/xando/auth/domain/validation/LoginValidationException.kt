package com.xando.auth.domain.validation

/**
 * Исключение валидации данных для входа.
 *
 * @property errors Список ошибок валидации.
 */
internal class LoginValidationException(
    val errors: List<LoginValidationError>,
    message: String = "Login validation exception"
) : Exception(message)

/**
 * Типы ошибок валидации логина.
 */
internal enum class LoginValidationError {
    /** Поле логина пустое. */
    LoginEmpty,
    /** Пароль короче минимальной длины. */
    PasswordTooShort
}
