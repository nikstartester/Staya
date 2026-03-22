package com.xando.auth.ui.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Состояние UI экрана входа.
 *
 * Используется как единый источник истины для отрисовки UI.
 */
internal sealed interface LoginUiState : Parcelable {

    /**
     * Базовое состояние экрана, в котором пользователь вводит данные.
     *
     * @property loginCredentials Текущие введённые данные для входа.
     * @property isLoginEnabled Флаг, указывающий, доступно ли действие входа.
     */
    @Parcelize
    data class Idle(
        val loginCredentials: LoginCredentials,
        val isLoginEnabled: Boolean = false
    ) : LoginUiState

    /**
     * Состояние выполнения авторизации.
     *
     * Отображается во время выполнения запроса на вход.
     *
     * @property loginCredentials Данные, используемые для авторизации.
     */
    @Parcelize
    data class Loading(
        val loginCredentials: LoginCredentials,
    ) : LoginUiState

    /**
     * Состояние ошибки авторизации.
     *
     * @property loginCredentials Данные, введённые пользователем.
     * @property message Текст ошибки для отображения пользователю.
     */
    @Parcelize
    data class Error(
        val loginCredentials: LoginCredentials,
        val message: String
    ) : LoginUiState

    /**
     * Состояние успешной авторизации.
     */
    @Parcelize
    data object Success : LoginUiState
}