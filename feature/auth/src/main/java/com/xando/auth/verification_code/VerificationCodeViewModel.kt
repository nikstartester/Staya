package com.xando.auth.verification_code

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.design.ui.theme.StayaString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
internal class VerificationCodeViewModel @Inject constructor(
    serverErrors: Flow<VerificationCodeError>,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val KEY_STATE = "VerificationCodeUiState"
        const val CODE_LENGTH = 4
        const val RESEND_CODE_TIMEOUT_SECONDS = 60
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        VerificationCodeUiState(timeLeftSeconds = RESEND_CODE_TIMEOUT_SECONDS)
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<VerificationCodeUiState> = _uiState.asStateFlow()

    private val _events = Channel<VerificationCodeEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<VerificationCodeEvent> = _events.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        if (_uiState.value.isTimerRunning) {
            startTimer()
        }

        viewModelScope.launch {
            serverErrors.collect(::applyServerError)
        }
    }

    /**@SelfDocumented*/
    fun onBottomSheetShown() {
        _uiState.update {
            it.copy(
                code = "",
                timeLeftSeconds = RESEND_CODE_TIMEOUT_SECONDS,
                errorText = null
            )
        }
        startTimer()
    }

    /**@SelfDocumented*/
    fun onCodeChanged(code: String) {
        if (code.length > CODE_LENGTH || !code.all { it.isDigit() }) return

        _uiState.update {
            it.copy(
                code = code,
                errorText = null
            )
        }

        if (code.length == CODE_LENGTH) {
            _events.trySend(VerificationCodeEvent.CodeConfirmed(code))
        }
    }

    /**@SelfDocumented*/
    fun onResendCodeClick(): Boolean {
        val state = _uiState.value
        if (state.isLoading || state.isTimerRunning) return false

        _uiState.update {
            it.copy(
                timeLeftSeconds = RESEND_CODE_TIMEOUT_SECONDS,
                errorText = null
            )
        }
        startTimer()
        return true
    }

    /**
     * Применяет флаг выполнения запроса, пришедший с внешнего экрана.
     */
    fun updateLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    /**
     * Применяет ошибку, пришедшую с сервера.
     *
     * Ошибка приходит одноразовым событием и дальше живёт в состоянии шторки: гаснет при вводе кода
     * и при повторной отправке. Пересоздание экрана её не переигрывает.
     */
    private fun applyServerError(error: VerificationCodeError) {
        if (error.isResendAllowed) timerJob?.cancel()

        _uiState.update {
            it.copy(
                errorText = error.errorText,
                timeLeftSeconds = if (error.isResendAllowed) 0 else it.timeLeftSeconds
            )
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        if (_uiState.value.timeLeftSeconds <= 0) return

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0) {
                delay(1000L)
                _uiState.update { state ->
                    if (state.timeLeftSeconds > 0) {
                        state.copy(timeLeftSeconds = state.timeLeftSeconds - 1)
                    } else {
                        state
                    }
                }
            }
        }
    }
}

/**@SelfDocumented*/
internal sealed interface VerificationCodeEvent {
    data class CodeConfirmed(val code: String) : VerificationCodeEvent
}

/**
 * Ответ сервера на ввод кода подтверждения.
 *
 * @property errorText Текст ошибки под полем ввода кода.
 * @property isResendAllowed `true`, если сервер сообщил, что текущий код непригоден и нужен новый.
 *   В этом случае таймер останавливается досрочно, иначе запросить новый код будет нечем.
 */
internal data class VerificationCodeError(
    val errorText: StayaString,
    val isResendAllowed: Boolean,
)

/**@SelfDocumented*/
@Parcelize
internal data class VerificationCodeUiState(
    val code: String = "",
    val timeLeftSeconds: Int = 0,
    val isLoading: Boolean = false,
    val errorText: StayaString? = null,
) : Parcelable {
    val isTimerRunning: Boolean
        get() = timeLeftSeconds > 0
}
