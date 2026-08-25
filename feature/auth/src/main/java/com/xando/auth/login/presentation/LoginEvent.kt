package com.xando.auth.login.presentation

import com.xando.design.ui.snackbar.StayaSnackbarData

/**
 * Одноразовые события экрана входа.
 */
internal sealed interface LoginEvent {

    /**
     * Ошибка авторизации, которую нужно показать пользователю.
     */
    data class ShowSnackbar(val snackbarData: StayaSnackbarData) : LoginEvent
}