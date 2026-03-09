package com.xando.auth.ui.sign_up

import android.net.Uri
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * Контракт для накопления данных регистрации между шагами флоу.
 */
internal interface SignUpFlowCoordinator {
    /**
     * Возвращает текущий снимок введённых данных регистрации.
     */
    fun getSnapshot(): SignUpFlowData

    /**
     * Сохраняет данные первого шага регистрации.
     */
    fun updateIntroduction(
        firstName: String,
        lastName: String,
        photoUri: Uri?,
    )

    /**
     * Сохраняет текст о пользователе.
     */
    fun updateAbout(description: String)

    /**
     * Сохраняет логин пользователя.
     */
    fun updateLogin(login: String)

    /**
     * Сохраняет e-mail и пароль пользователя.
     */
    fun updateEmailPassword(
        email: String,
        password: String,
        repeatPassword: String,
    )
}

@ActivityRetainedScoped
internal class SignUpFlowCoordinatorImpl @Inject constructor() : SignUpFlowCoordinator {

    private var state = SignUpFlowData()

    /**@SelfDocumented*/
    override fun getSnapshot(): SignUpFlowData = state.copy()

    /**@SelfDocumented*/
    override fun updateIntroduction(
        firstName: String,
        lastName: String,
        photoUri: Uri?,
    ) {
        state = state.copy(
            firstName = firstName,
            lastName = lastName,
            photoUri = photoUri
        )
    }

    /**@SelfDocumented*/
    override fun updateAbout(description: String) {
        state = state.copy(description = description)
    }

    /**@SelfDocumented*/
    override fun updateLogin(login: String) {
        state = state.copy(login = login)
    }

    /**@SelfDocumented*/
    override fun updateEmailPassword(
        email: String,
        password: String,
        repeatPassword: String,
    ) {
        state = state.copy(
            email = email,
            password = password,
            repeatPassword = repeatPassword
        )
    }
}

/**
 * Полный набор данных, собранных в рамках флоу регистрации.
 */
internal data class SignUpFlowData(
    val firstName: String = "",
    val lastName: String = "",
    val photoUri: Uri? = null,
    val description: String = "",
    val login: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
)