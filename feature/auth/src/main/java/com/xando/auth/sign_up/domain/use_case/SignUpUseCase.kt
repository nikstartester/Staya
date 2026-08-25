package com.xando.auth.sign_up.domain.use_case

import com.xando.core.models.auth.data.SignUpData
import com.xando.data.auth.AuthRepository
import javax.inject.Inject

/**
 * Юзкейс авторизации
 */
internal class SignUpUseCase @Inject constructor(private val repository: AuthRepository) {

    /**
     * Регистрирует пользователя.
     *
     * @param signUpData Данные для регистрации.
     */
    suspend fun signUp(signUpData: SignUpData) {
        repository.signUp(signUpData)
    }

    /**
     * Подтверждает e-mail кодом из письма.
     *
     * @param email E-mail пользователя.
     * @param code Код подтверждения.
     */
    suspend fun verifyEmail(email: String, code: String) {
        repository.verifyEmail(email = email, code = code)
    }

    /**
     * Переотправляет код подтверждения на e-mail.
     *
     * @param email E-mail пользователя.
     */
    suspend fun resendCode(email: String) {
        repository.resendCode(email)
    }
}