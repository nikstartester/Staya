package com.xando.auth.ui.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Состояние UI экрана входа.
 *
 * @property loginCredentials Текущие введенные данные для входа.
 * @property isLoginEnabled Доступность действия входа.
 * @property isLoading Флаг выполнения запроса авторизации.
 */
@Parcelize
internal data class LoginUiState(
    val loginCredentials: LoginCredentials = LoginCredentials(),
    val isLoginEnabled: Boolean = false,
    val isLoading: Boolean = false
) : Parcelable
