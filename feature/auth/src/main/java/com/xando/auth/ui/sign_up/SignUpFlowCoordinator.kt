package com.xando.auth.ui.sign_up

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
        photoUri: String?,
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

    private val state = SignUpFlowData()

    /**@SelfDocumented*/
    override fun getSnapshot(): SignUpFlowData = state.copy()

    /**@SelfDocumented*/
    override fun updateIntroduction(
        firstName: String,
        lastName: String,
        photoUri: String?,
    ) {
        state.firstName = firstName
        state.lastName = lastName
        state.photoUri = photoUri
    }

    /**@SelfDocumented*/
    override fun updateAbout(description: String) {
        state.description = description
    }

    /**@SelfDocumented*/
    override fun updateLogin(login: String) {
        state.login = login
    }

    /**@SelfDocumented*/
    override fun updateEmailPassword(
        email: String,
        password: String,
        repeatPassword: String,
    ) {
        state.email = email
        state.password = password
        state.repeatPassword = repeatPassword
    }
}

/**
 * Полный набор данных, собранных в рамках флоу регистрации.
 */
internal data class SignUpFlowData(
    var firstName: String = "",
    var lastName: String = "",
    var photoUri: String? = null,
    var description: String = "",
    var login: String = "",
    var email: String = "",
    var password: String = "",
    var repeatPassword: String = "",
)
