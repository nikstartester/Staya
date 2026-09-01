package com.xando.auth.sign_up

import com.xando.design.ui.theme.StayaString
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * Отправка событий шагам флоу регистрации.
 *
 * Ошибка сервера — одноразовое событие, а не состояние: её нужно доставить ровно один раз, дальше она
 * живёт в состоянии шага и гаснет по правилам этого шага.
 *
 * Получатели инжектят не эту шину, а конкретный поток, который их касается — см. [com.xando.auth.sign_up.di.SignUpFlowModule].
 * У каждого потока ровно один получатель, буфер не ограничен, поэтому событие дождётся получателя,
 * даже если тот ещё не создан.
 */
internal interface SignUpFlowFeedback {

    /**
     * Ошибки шага ввода логина.
     */
    val loginErrors: Flow<LoginError>

    /**
     * Ошибки шага ввода e-mail и пароля.
     */
    val emailPasswordErrors: Flow<EmailPasswordErrors>

    /**
     * Отправляет ошибку на шаг ввода логина.
     */
    fun sendLoginError(error: StayaString)

    /**
     * Отправляет ошибки на шаг ввода e-mail и пароля.
     *
     * @param emailError Ошибка e-mail или `null`, если поле в порядке.
     * @param passwordError Ошибка пароля или `null`, если поле в порядке.
     */
    fun sendEmailPasswordErrors(emailError: StayaString?, passwordError: StayaString?)

    /**
     * Выбрасывает недоставленные события. Нужен при старте нового флоу, чтобы ошибка прошлой попытки
     * не всплыла в следующей: область жизни шины шире одного прохода регистрации.
     */
    fun clear()
}

/**
 * Ошибка шага ввода логина.
 *
 * @property text Текст ошибки под полем логина.
 */
internal data class LoginError(val text: StayaString)

/**
 * Ошибки шага ввода e-mail и пароля.
 *
 * @property emailError Ошибка e-mail или `null`, если ошибки нет.
 * @property passwordError Ошибка пароля или `null`, если ошибки нет.
 */
internal data class EmailPasswordErrors(
    val emailError: StayaString?,
    val passwordError: StayaString?,
)

/**@SelfDocumented*/
@ActivityRetainedScoped
internal class SignUpFlowFeedbackImpl @Inject constructor() : SignUpFlowFeedback {

    private val channels = mutableListOf<Channel<*>>()

    private val loginErrorsChannel = eventChannel<LoginError>()
    private val emailPasswordErrorsChannel = eventChannel<EmailPasswordErrors>()

    /**@SelfDocumented*/
    override val loginErrors: Flow<LoginError> = loginErrorsChannel.receiveAsFlow()

    /**@SelfDocumented*/
    override val emailPasswordErrors: Flow<EmailPasswordErrors> = emailPasswordErrorsChannel.receiveAsFlow()

    /**@SelfDocumented*/
    override fun sendLoginError(error: StayaString) {
        loginErrorsChannel.trySend(LoginError(error))
    }

    /**@SelfDocumented*/
    override fun sendEmailPasswordErrors(emailError: StayaString?, passwordError: StayaString?) {
        emailPasswordErrorsChannel.trySend(EmailPasswordErrors(emailError, passwordError))
    }

    /**@SelfDocumented*/
    override fun clear() {
        channels.forEach { channel ->
            while (channel.tryReceive().isSuccess) Unit
        }
    }

    /**
     * Создаёт канал события и регистрирует его для [clear], чтобы новый канал нельзя было забыть
     * добавить в очистку.
     */
    private fun <T> eventChannel(): Channel<T> =
        Channel<T>(capacity = Channel.BUFFERED).also { channels += it }
}
