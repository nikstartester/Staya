package com.xando.auth.sign_up.pages.login

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.auth.sign_up.LoginError
import com.xando.auth.sign_up.SignUpFlowCoordinator
import com.xando.auth.sign_up.components.BottomSectionAction
import com.xando.auth.sign_up.domain.use_case.CheckLoginAvailabilityUseCase
import com.xando.core.api_models.ApiException
import com.xando.design.ui.theme.StayaString
import com.xando.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

/** Минимальная длина логина. */
internal const val MIN_LOGIN_LENGTH = 4

/**
 * ViewModel шага выбора логина.
 */
@HiltViewModel
internal class SignUpLoginViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    private val checkLoginAvailabilityUseCase: CheckLoginAvailabilityUseCase,
    serverErrors: Flow<LoginError>,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "SignUpLoginViewModel"

        private val LOGIN_CHARS_REGEX = Regex("^[A-Za-z0-9_]*$")

        /**
         * Пауза перед запросом доступности логина. Гасит поток запросов при быстром вводе:
         * уходит только последнее значение.
         */
        private const val AVAILABILITY_CHECK_DEBOUNCE_MILLIS = 300L

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

    init {
        // Состояние проверки не переживает смерть процесса: до ответа перепроверки (её запустит
        // observeLoginAvailability) показываем прогресс, иначе поле выглядит проверенным.
        if (isCheckable(_uiState.value.login)) {
            _uiState.update { it.copy(availability = LoginAvailability.CHECKING) }
        }

        observeLoginAvailability()

        viewModelScope.launch {
            serverErrors.collect { error ->
                _uiState.update {
                    it.copy(loginError = error.text, availability = LoginAvailability.NONE)
                }
            }
        }
    }

    /**
     * Обновляет логин и проверяет его локально. Недопустимый символ подсвечивает подсказку сразу:
     * правило уже нарушено, дальнейший ввод его не исправит. Длину во время набора не проверяем —
     * недописанный логин ещё не ошибка. Логин, прошедший локальную проверку, переводит поле
     * в состояние проверки доступности.
     */
    fun updateLogin(login: String) {
        val action = if (login.isBlank()) BottomSectionAction.SKIP else BottomSectionAction.CONTINUE
        val availability =
            if (isCheckable(login)) LoginAvailability.CHECKING else LoginAvailability.NONE

        _uiState.update {
            it.copy(
                login = login,
                loginError = null,
                isLoginRulesViolated = !LOGIN_CHARS_REGEX.matches(login),
                action = action,
                availability = availability
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
     * Проверяет логин по правилам экрана перед переходом дальше. Дальше пускает только пустой логин
     * (шаг пропущен) или логин, который сервер подтвердил свободным.
     */
    private fun validate(): Boolean {
        val state = _uiState.value

        if (state.login.isBlank()) return true

        if (!isCheckable(state.login)) {
            _uiState.update {
                it.copy(isLoginRulesViolated = true, availability = LoginAvailability.NONE)
            }
            return false
        }

        return state.loginError == null && state.availability == LoginAvailability.AVAILABLE
    }

    /**
     * Запускает проверку логина на сервере при каждом новом значении поля. Запрос предыдущего
     * значения отменяется: результат нужен только для последнего введённого логина.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeLoginAvailability() {
        viewModelScope.launch {
            uiState
                .map { it.login }
                .distinctUntilChanged()
                .debounce(AVAILABILITY_CHECK_DEBOUNCE_MILLIS)
                .mapLatest { login -> if (isCheckable(login)) checkAvailability(login) else null }
                .collect { result -> result?.let(::applyAvailabilityResult) }
        }
    }

    private suspend fun checkAvailability(login: String): AvailabilityResult {
        return try {
            if (checkLoginAvailabilityUseCase.isAvailable(login)) AvailabilityResult.AVAILABLE
            else AvailabilityResult.TAKEN
        } catch (ex: ApiException) {
            Log.w(TAG, ex)
            AvailabilityResult.FAILURE
        }
    }

    private fun applyAvailabilityResult(result: AvailabilityResult) {
        val loginError = when (result) {
            AvailabilityResult.AVAILABLE -> null
            AvailabilityResult.TAKEN -> StayaString.Res(R.string.auth_sign_up_error_login_exists)
            AvailabilityResult.FAILURE -> StayaString.Res(R.string.auth_sign_up_error_login_check_failed)
        }

        val availability =
            if (result == AvailabilityResult.AVAILABLE) {
                LoginAvailability.AVAILABLE
            } else {
                LoginAvailability.NONE
            }

        _uiState.update { it.copy(loginError = loginError, availability = availability) }
    }

    /**
     * Возвращает `true`, если логин прошёл локальную проверку и его есть смысл проверять на сервере.
     */
    private fun isCheckable(login: String): Boolean =
        login.length >= MIN_LOGIN_LENGTH && LOGIN_CHARS_REGEX.matches(login)
}

/**
 * Итог проверки логина на сервере.
 */
private enum class AvailabilityResult {
    /** Логин свободен. */
    AVAILABLE,

    /** Логин уже занят. */
    TAKEN,

    /** Проверить логин не удалось. */
    FAILURE
}

/**
 * Состояние проверки логина на доступность.
 */
internal enum class LoginAvailability {
    /** Проверка не идёт: логин пуст, не прошёл локальную проверку или уже отклонён сервером. */
    NONE,

    /** Идёт запрос доступности логина. */
    CHECKING,

    /** Сервер подтвердил, что логин свободен. */
    AVAILABLE
}

/**
 * Состояние шага ввода логина.
 *
 * @property login Введённый логин.
 * @property loginError Ошибка сервера под полем логина или `null`, если ошибки нет.
 * @property isLoginRulesViolated Признак нарушения локальных правил логина: подсказка под полем
 *   подсвечивается цветом ошибки.
 * @property action Действие нижней кнопки.
 * @property minLoginLength Минимальная длина логина для подсказки под полем.
 * @property availability Состояние проверки логина на доступность.
 *   Не сохраняется при смерти процесса — запрос его не переживает.
 */
@Parcelize
internal data class SignUpLoginUiState(
    val login: String = "",
    val loginError: StayaString? = null,
    val isLoginRulesViolated: Boolean = false,
    val action: BottomSectionAction = BottomSectionAction.SKIP,
    val minLoginLength: Int = MIN_LOGIN_LENGTH,
    @IgnoredOnParcel
    val availability: LoginAvailability = LoginAvailability.NONE,
) : Parcelable

/**
 * Одноразовые события шага ввода логина.
 */
sealed interface SignUpLoginEvent {
    data object NavigateNext : SignUpLoginEvent
}
