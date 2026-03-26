package com.xando.auth.presentation.login

import android.os.Parcelable
import com.xando.design.ui.theme.StayaString
import kotlinx.parcelize.Parcelize

/**
 * Состояние UI экрана входа.
 *
 * @property loginCredentials Текущие введенные данные для входа.
 * @property loginError Ошибка ввода логина.
 * @property passwordError Ошибка ввода пароля.
 * @property isLoading Флаг выполнения запроса авторизации.
 */
@Parcelize
internal data class LoginUiState(
    val loginCredentials: LoginCredentials = LoginCredentials(),
    val loginError: StayaString? = null,
    val passwordError: StayaString? = null,
    val isLoading: Boolean = false
) : Parcelable
