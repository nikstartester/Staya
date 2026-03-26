package com.xando.auth.presentation.sign_up.pages.email_password

import android.os.Parcelable
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.xando.auth.domain.validation.AuthValidationRules
import com.xando.auth.presentation.sign_up.SignUpFlowCoordinator
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
 * ViewModel шага ввода e-mail и пароля.
 */
@HiltViewModel
internal class SignUpEmailPasswordViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "SignUpEmailPasswordUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        SignUpEmailPasswordUiState(minPasswordLength = AuthValidationRules.MIN_PASSWORD_LENGTH)
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<SignUpEmailPasswordUiState> = _uiState.asStateFlow()

    private val _events = Channel<SignUpEmailPasswordEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpEmailPasswordEvent> = _events.receiveAsFlow()

    /**
     * Обновляет e-mail и очищает ошибку поля.
     */
    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = null,
            )
        }
    }

    /**
     * Обновляет пароль и очищает связанные ошибки.
     */
    fun updatePassword(password: String) {
        val showPasswordButtonVisible = password.isNotEmpty() || _uiState.value.repeatPassword.isNotEmpty()
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                repeatPasswordError = if (it.repeatPassword.isEmpty()) null else it.repeatPasswordError,
                showPasswordButtonVisible = showPasswordButtonVisible
            )
        }
    }

    /**
     * Обновляет повтор пароля и очищает ошибку поля.
     */
    fun updateRepeatPassword(repeatPassword: String) {
        val showPasswordButtonVisible = _uiState.value.password.isNotEmpty() || repeatPassword.isNotEmpty()
        _uiState.update {
            it.copy(
                repeatPassword = repeatPassword,
                repeatPasswordError = null,
                showPasswordButtonVisible = showPasswordButtonVisible
            )
        }
    }

    /**
     * Переключает видимость пароля.
     */
    fun updatePasswordVisibility(isPasswordVisible: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = isPasswordVisible) }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        if (!validate()) return

        updateCoordinator()
        _events.trySend(SignUpEmailPasswordEvent.NavigateNext)
    }

    private fun updateCoordinator() {
        val state = _uiState.value
        coordinator.updateEmailPassword(
            email = state.email,
            password = state.password,
            repeatPassword = state.repeatPassword,
        )
    }

    /**
     * Проверяет корректность e-mail и паролей перед переходом дальше.
     */
    private fun validate(): Boolean {
        val state = _uiState.value

        val emailError = when {
            state.email.isBlank() -> ""
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> ""
            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> ""
            state.password.length < AuthValidationRules.MIN_PASSWORD_LENGTH -> ""
            else -> null
        }

        val repeatPasswordError = when {
            state.repeatPassword.isBlank() -> ""
            state.password != state.repeatPassword -> ""
            else -> null
        }

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                repeatPasswordError = repeatPasswordError,
            )
        }

        return emailError == null && passwordError == null && repeatPasswordError == null
    }
}

/**@SelfDocumented*/
@Parcelize
internal data class SignUpEmailPasswordUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null,
    val showPasswordButtonVisible: Boolean = false,
    val minPasswordLength: Int = 6,
) : Parcelable

/**
 * Одноразовые события шага ввода e-mail и пароля.
 */
sealed interface SignUpEmailPasswordEvent {
    data object NavigateNext : SignUpEmailPasswordEvent
}