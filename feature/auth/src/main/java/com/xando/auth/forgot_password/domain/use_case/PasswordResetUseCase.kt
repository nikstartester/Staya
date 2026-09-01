package com.xando.auth.forgot_password.domain.use_case

import com.xando.data.auth.AuthRepository
import javax.inject.Inject

/**
 * Юзкейс сброса пароля.
 */
internal class PasswordResetUseCase @Inject constructor(private val repository: AuthRepository) {

    /**
     * Запрашивает код подтверждения для сброса пароля.
     *
     * @param email E-mail пользователя.
     */
    suspend fun requestReset(email: String) {
        repository.requestPasswordReset(email)
    }

    /**
     * Устанавливает новый пароль по коду из письма.
     *
     * @param email E-mail пользователя.
     * @param code Код подтверждения.
     * @param newPassword Новый пароль.
     */
    suspend fun confirmReset(email: String, code: String, newPassword: String) {
        repository.confirmPasswordReset(email = email, code = code, newPassword = newPassword)
    }
}
