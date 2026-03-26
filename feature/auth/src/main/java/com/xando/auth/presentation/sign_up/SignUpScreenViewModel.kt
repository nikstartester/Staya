package com.xando.auth.presentation.sign_up

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * ViewModel верхнего уровня для всего signup-флоу.
 */
@HiltViewModel
internal class SignUpScreenViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private companion object {
        const val KEY_FLOW_DATA = "SignUpScreenFlowData"
    }


    private val _events = Channel<SignUpFlowEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpFlowEvent> = _events.receiveAsFlow()

    init {
        savedStateHandle.get<SignUpFlowData>(KEY_FLOW_DATA)?.let {
            coordinator.restoreFrom(it)
        }
    }

    /**
     * Обработка пееродов дальше
     */
    fun onContinueClick() {
        savedStateHandle[KEY_FLOW_DATA] = coordinator.getSnapshot()
    }


    /**
     * Завершает флоу регистрации и публикует собранные данные.
     */
    fun onFinalStepCompleted() {
        _events.trySend(SignUpFlowEvent.SignUpCompleted(coordinator.getSnapshot()))
    }
}

/**
 * Одноразовые события верхнего уровня для всего signup-флоу.
 */
internal sealed interface SignUpFlowEvent {
    data class SignUpCompleted(val data: SignUpFlowData) : SignUpFlowEvent
}