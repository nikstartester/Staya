package com.xando.auth.domain

import com.xando.auth.domain.validation.AuthValidationRules.MIN_PASSWORD_LENGTH
import com.xando.auth.domain.validation.LoginValidationError
import com.xando.auth.domain.validation.LoginValidationException
import com.xando.data.auth.AuthRepository
import javax.inject.Inject

/**
 * Юзкейс авторизации
 */
internal class LoginUseCase @Inject constructor(private val repository: AuthRepository) {

    /**
     * Выполняет валидацию и авторизацию пользователя.
     *
     * @param login Email или логин пользователя.
     * @param password Пароль.
     * @throws LoginValidationException При невалидных входных данных.
     */
    @Throws(LoginValidationException::class)
    suspend fun login(login: String, password: String) {
        validateOrThrow(login, password)
        repository.login(emailOrLogin = login, password = password)
    }

    @Throws(LoginValidationException::class)
    private fun validateOrThrow(login: String, password: String) {
        val errors = mutableListOf<LoginValidationError>()
        if (login.isBlank()) errors += LoginValidationError.LoginEmpty
        if (password.length < MIN_PASSWORD_LENGTH) errors += LoginValidationError.PasswordTooShort

        if (errors.isNotEmpty()) throw LoginValidationException(errors)
    }
}