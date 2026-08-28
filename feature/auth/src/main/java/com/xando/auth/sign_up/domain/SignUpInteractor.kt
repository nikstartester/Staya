package com.xando.auth.sign_up.domain

import android.net.Uri
import com.xando.auth.sign_up.di.SignUpAvatar
import com.xando.auth.sign_up.domain.use_case.AvatarUploadUseCase
import com.xando.auth.sign_up.domain.use_case.SignUpUseCase
import com.xando.core.models.auth.data.SignUpData
import javax.inject.Inject

/**
 * Агрегатор методов регистрации.
 */
internal class SignUpInteractor @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    @param:SignUpAvatar private val avatarUploadUseCase: AvatarUploadUseCase
) {
    /**
     * Регистрирует пользователя.
     *
     * @param signUpData Данные для регистрации.
     */
    suspend fun signUp(signUpData: SignUpData) = signUpUseCase.signUp(signUpData)

    /**
     * Подтверждает e-mail кодом из письма.
     *
     * @param email E-mail пользователя.
     * @param code Код подтверждения.
     */
    suspend fun verifyEmail(email: String, code: String) = signUpUseCase.verifyEmail(email, code)

    /**
     * Переотправляет код подтверждения на e-mail.
     *
     * @param email E-mail пользователя.
     */
    suspend fun resendCode(email: String) = signUpUseCase.resendCode(email)


    /**
     * Ставит в очередь загрузку фотографии профиля. Требует уже подтверждённого e-mail.
     *
     * @param photoUri Uri выбранной пользователем фотографии.
     */
    fun uploadAvatar(photoUri: Uri) = avatarUploadUseCase.uploadAvatar(photoUri)
}