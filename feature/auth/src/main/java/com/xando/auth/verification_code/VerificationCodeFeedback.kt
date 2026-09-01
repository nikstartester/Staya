package com.xando.auth.verification_code

import com.xando.design.ui.theme.StayaString
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * Отправка ответов сервера в шторку ввода кода подтверждения.
 *
 * Ошибка сервера — одноразовое событие, а не состояние: её нужно доставить ровно один раз, дальше она
 * живёт в состоянии шторки и гаснет по её правилам.
 *
 * Шина общая для всех флоу с подтверждением кода: регистрации и сброса пароля. Получатель инжектит
 * не саму шину, а поток [errors] — см. [com.xando.auth.verification_code.di.VerificationCodeModule].
 * Получатель ровно один, буфер не ограничен, поэтому событие дождётся получателя, даже если тот ещё
 * не создан.
 */
internal interface VerificationCodeFeedback {

    /**
     * Ошибки кода подтверждения.
     */
    val errors: Flow<VerificationCodeError>

    /**
     * Отправляет ошибку в шторку ввода кода подтверждения.
     *
     * @param error Текст ошибки под полем ввода кода.
     * @param isResendAllowed `true`, если текущий код непригоден и повторную отправку нужно разблокировать.
     */
    fun sendError(error: StayaString, isResendAllowed: Boolean)

    /**
     * Выбрасывает недоставленные события. Нужен при старте нового флоу, чтобы ошибка прошлой попытки
     * не всплыла в следующей: область жизни шины шире одного прохода флоу.
     */
    fun clear()
}

/**@SelfDocumented*/
@ActivityRetainedScoped
internal class VerificationCodeFeedbackImpl @Inject constructor() : VerificationCodeFeedback {

    private val errorsChannel = Channel<VerificationCodeError>(capacity = Channel.BUFFERED)

    /**@SelfDocumented*/
    override val errors: Flow<VerificationCodeError> = errorsChannel.receiveAsFlow()

    /**@SelfDocumented*/
    override fun sendError(error: StayaString, isResendAllowed: Boolean) {
        errorsChannel.trySend(VerificationCodeError(error, isResendAllowed))
    }

    /**@SelfDocumented*/
    override fun clear() {
        while (errorsChannel.tryReceive().isSuccess) Unit
    }
}
