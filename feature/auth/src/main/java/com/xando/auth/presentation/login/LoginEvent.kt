package com.xando.auth.presentation.login

import com.xando.design.ui.theme.StayaString

/**
 * Одноразовые события экрана входа.
 */
internal sealed interface LoginEvent {

    /**
     * Ошибка авторизации, которую нужно показать пользователю.
     */
    data class ShowError(val message: StayaString) : LoginEvent
}