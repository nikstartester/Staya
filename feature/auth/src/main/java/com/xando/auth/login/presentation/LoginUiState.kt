package com.xando.auth.login.presentation

import android.os.Parcelable
import com.xando.design.ui.theme.StayaString
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Состояние UI экрана входа.
 *
 * @property loginCredentials Текущие введенные данные для входа.
 * @property loginError Ошибка ввода логина.
 * @property passwordError Ошибка ввода пароля.
 * @property isLoading Флаг выполнения запроса авторизации. Не сохраняется при смерти процесса - запрос его не переживает.
 */
@Parcelize
internal data class LoginUiState(
    val loginCredentials: LoginCredentials = LoginCredentials(),
    val loginError: StayaString? = null,
    val passwordError: StayaString? = null,
    @IgnoredOnParcel
    val isLoading: Boolean = false
) : Parcelable
