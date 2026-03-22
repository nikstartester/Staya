package com.xando.auth.ui.login

/**
 * Одноразовые события экрана входа.
 */
internal sealed interface LoginEvent {
    /**
     * Успешная авторизация, требующая перехода на следующий экран.
     */
    data object LoginSuccess : LoginEvent

    /**
     * Ошибка авторизации, которую нужно показать пользователю.
     */
    data class ShowError(val message: String) : LoginEvent
}