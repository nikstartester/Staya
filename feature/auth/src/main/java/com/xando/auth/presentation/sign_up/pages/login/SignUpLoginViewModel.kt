package com.xando.auth.presentation.sign_up.pages.login

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.xando.auth.presentation.sign_up.SignUpFlowCoordinator
import com.xando.auth.presentation.sign_up.components.BottomSectionAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/**
 * ViewModel шага выбора логина.
 */
@HiltViewModel
internal class SignUpLoginViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private val LOGIN_REGEX = Regex("^[A-Za-z0-9_]{$MIN_LOGIN_LENGTH,}$")
        private const val MIN_LOGIN_LENGTH = 4

        private const val KEY_STATE = "SignUpLoginUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        SignUpLoginUiState(minLoginLength = MIN_LOGIN_LENGTH)
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<SignUpLoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<SignUpLoginEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpLoginEvent> = _events.receiveAsFlow()

    /**
     * Обновляет логин и очищает ошибку поля.
     */
    fun updateLogin(login: String) {
        val action = if (login.isBlank()) BottomSectionAction.SKIP else BottomSectionAction.CONTINUE
        _uiState.update {
            it.copy(
                login = login,
                loginError = null,
                action = action
            )
        }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        if (!validate()) return

        updateCoordinator()
        _events.trySend(SignUpLoginEvent.NavigateNext)
    }

    private fun updateCoordinator() {
        val state = _uiState.value
        coordinator.updateLogin(login = state.login)
    }

    /**
     * Проверяет логин по правилам экрана перед переходом дальше.
     */
    private fun validate(): Boolean {
        val login = _uiState.value.login

        if (login.isBlank()) return true

        val loginError = if (LOGIN_REGEX.matches(login)) null
        else ""

        _uiState.update { it.copy(loginError = loginError) }

        return loginError == null
    }
}

/**@SelfDocumented*/
@Parcelize
internal data class SignUpLoginUiState(
    val login: String = "",
    val loginError: String? = null,
    val action: BottomSectionAction = BottomSectionAction.SKIP,
    val minLoginLength: Int = 4
) : Parcelable

/**
 * Одноразовые события шага ввода логина.
 */
sealed interface SignUpLoginEvent {
    data object NavigateNext : SignUpLoginEvent
}