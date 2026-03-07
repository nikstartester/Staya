package com.xando.auth.ui.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
internal class SignUpScreenViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
) : ViewModel() {

    /**
     * Одноразовые события верхнего уровня для всего signup-флоу.
     */
    sealed interface Event {
        data class SignUpCompleted(val data: SignUpFlowData) : Event
    }

    private val _events = Channel<Event>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    /**
     * Завершает флоу регистрации и публикует собранные данные.
     */
    fun onFinalStepCompleted() {
        _events.trySend(Event.SignUpCompleted(coordinator.getSnapshot()))
    }
}
