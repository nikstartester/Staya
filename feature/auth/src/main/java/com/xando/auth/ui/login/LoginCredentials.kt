package com.xando.auth.ui.login

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Данные, вводимые пользователем для авторизации.
 *
 * @property login Email или логин пользователя.
 * @property password Пароль пользователя.
 */
@Parcelize
internal data class LoginCredentials(
    val login: String = "",
    val password: String = ""
) : Parcelable