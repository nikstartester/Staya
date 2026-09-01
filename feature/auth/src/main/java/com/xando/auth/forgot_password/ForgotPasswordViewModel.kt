package com.xando.auth.forgot_password

import android.os.Parcelable
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.auth.forgot_password.domain.use_case.PasswordResetUseCase
import com.xando.auth.login.domain.validation.AuthValidationRules
import com.xando.auth.verification_code.VerificationCodeFeedback
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.RateLimitException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.core.api_models.ValidationException
import com.xando.data.auth.AuthValidationCodes
import com.xando.data.auth.AuthValidationCodes.CODE_EXPIRED
import com.xando.data.auth.AuthValidationCodes.CODE_MAX_ATTEMPTS
import com.xando.data.auth.AuthValidationCodes.CODE_NOT_FOUND
import com.xando.data.auth.AuthValidationCodes.INVALID_CODE
import com.xando.design.ui.snackbar.SnackbarType
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.design.ui.theme.StayaString
import com.xando.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import com.xando.core.design.R as RDesign

/**
 * ViewModel экрана сброса пароля.
 *
 * Пользователь заполняет e-mail и новый пароль на одном экране: сервер меняет пароль одним запросом
 * вместе с кодом, поэтому к моменту ввода кода пароль уже должен быть собран.
 */
@HiltViewModel
internal class ForgotPasswordViewModel @Inject constructor(
    private val useCase: PasswordResetUseCase,
    private val verificationFeedback: VerificationCodeFeedback,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val TAG = "ForgotPasswordViewModel"

        const val KEY_STATE = "ForgotPasswordUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        ForgotPasswordUiState(minPasswordLength = AuthValidationRules.MIN_PASSWORD_LENGTH)
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _events = Channel<ForgotPasswordEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<ForgotPasswordEvent> = _events.receiveAsFlow()

    init {
        verificationFeedback.clear()
    }

    /**
     * Обновляет e-mail и очищает ошибку поля.
     */
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    /**
     * Обновляет пароль и очищает связанные ошибки.
     */
    fun updatePassword(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                passwordError = null,
                repeatPasswordError = if (it.repeatPassword.isEmpty()) null else it.repeatPasswordError,
                showPasswordButtonVisible = password.isNotEmpty() || it.repeatPassword.isNotEmpty()
            )
        }
    }

    /**
     * Обновляет повтор пароля и очищает ошибку поля.
     */
    fun updateRepeatPassword(repeatPassword: String) {
        _uiState.update {
            it.copy(
                repeatPassword = repeatPassword,
                repeatPasswordError = null,
                showPasswordButtonVisible = it.password.isNotEmpty() || repeatPassword.isNotEmpty()
            )
        }
    }

    /**@SelfDocumented*/
    fun updatePasswordVisibility(isPasswordVisible: Boolean) {
        _uiState.update { it.copy(isPasswordVisible = isPasswordVisible) }
    }

    /**
     * Запрашивает код подтверждения на введённый e-mail и открывает шторку ввода кода.
     */
    fun onContinueClick() {
        if (_uiState.value.isLoading) return
        if (!validate()) return

        _uiState.update { it.copy(isLoading = true) }

        val email = _uiState.value.email
        viewModelScope.launch {
            try {
                useCase.requestReset(email)
                _uiState.update { it.copy(isVerificationCodeBottomSheetVisible = true) }
            } catch (ex: ApiException) {
                catchRequestApiException(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(ForgotPasswordEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                Log.w(TAG, th)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**@SelfDocumented*/
    fun hideVerificationCodeBottomSheet() {
        _uiState.update { it.copy(isVerificationCodeBottomSheetVisible = false) }
    }

    /**
     * Меняет пароль по введённому коду. При успехе пользователь возвращается на экран входа.
     */
    fun onVerificationCodeConfirmed(code: String) {
        val state = _uiState.value
        if (state.isVerificationLoading) return

        _uiState.update { it.copy(isVerificationLoading = true) }

        viewModelScope.launch {
            try {
                useCase.confirmReset(email = state.email, code = code, newPassword = state.password)
                hideVerificationCodeBottomSheet()
                _events.trySend(
                    ForgotPasswordEvent.ShowSnackbar(
                        StayaSnackbarData(
                            type = SnackbarType.SUCCESS,
                            messageResId = R.string.auth_forgot_password_success,
                            iconRes = RDesign.drawable.design_ic_lock_24dp
                        )
                    )
                )
                _events.trySend(ForgotPasswordEvent.NavigateToLogin)
            } catch (ex: ApiException) {
                catchConfirmApiException(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(ForgotPasswordEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                // TODO: перейти на Timber
                Log.w(TAG, th)
            } finally {
                _uiState.update { it.copy(isVerificationLoading = false) }
            }
        }
    }

    /**
     * Переотправляет код подтверждения на e-mail.
     */
    fun onResendVerificationCode() {
        if (_uiState.value.isVerificationLoading) return

        _uiState.update { it.copy(isVerificationLoading = true) }

        val email = _uiState.value.email
        viewModelScope.launch {
            try {
                useCase.requestReset(email)
            } catch (ex: ApiException) {
                showErrorSnackbar(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(ForgotPasswordEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                // TODO: перейти на Timber
                Log.w(TAG, th)
            } finally {
                _uiState.update { it.copy(isVerificationLoading = false) }
            }
        }
    }

    /**
     * Проверяет e-mail и новый пароль перед запросом кода.
     */
    private fun validate(): Boolean {
        val state = _uiState.value

        val emailError = when {
            state.email.isBlank() -> StayaString.EMPTY
            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> {
                StayaString.Res(R.string.auth_sign_up_error_invalid_email)
            }

            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> StayaString.EMPTY
            state.password.length < AuthValidationRules.MIN_PASSWORD_LENGTH -> StayaString.EMPTY
            else -> null
        }

        val repeatPasswordError = when {
            state.repeatPassword.isBlank() -> StayaString.EMPTY
            state.password != state.repeatPassword -> StayaString.EMPTY
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

    private fun catchRequestApiException(ex: ApiException) {
        if (ex is ValidationException && ex.code == AuthValidationCodes.INVALID_EMAIL) {
            _uiState.update { it.copy(emailError = StayaString.Res(R.string.auth_sign_up_error_invalid_email)) }
            return
        }

        showErrorSnackbar(ex)
    }

    /**
     * Разбирает ошибку смены пароля: ошибка кода уходит в шторку, ошибка данных — под соответствующее поле.
     */
    private fun catchConfirmApiException(ex: ApiException) {
        val code = (ex as? ValidationException)?.code

        val errorResId = when (code) {
            INVALID_CODE -> R.string.auth_verification_code_error_invalid_code
            CODE_NOT_FOUND -> R.string.auth_verification_code_error_not_found
            CODE_EXPIRED -> R.string.auth_verification_code_error_expired
            CODE_MAX_ATTEMPTS -> R.string.auth_verification_code_error_max_attempts
            else -> null
        }

        if (errorResId != null) {
            // Досрочно разблокируем повторную отправку, только если код стал непригоден сам по себе.
            // При CODE_MAX_ATTEMPTS лимит исчерпан попытками пользователя — отсчёт дожидается конца.
            val isResendAllowed = code == CODE_NOT_FOUND || code == CODE_EXPIRED

            verificationFeedback.sendError(
                error = StayaString.Res(errorResId),
                isResendAllowed = isResendAllowed
            )
            return
        }

        when (code) {
            AuthValidationCodes.WEAK_PASSWORD -> {
                hideVerificationCodeBottomSheet()
                _uiState.update {
                    it.copy(passwordError = StayaString.Res(R.string.auth_sign_up_error_weak_password))
                }
            }

            AuthValidationCodes.INVALID_EMAIL -> {
                hideVerificationCodeBottomSheet()
                _uiState.update {
                    it.copy(emailError = StayaString.Res(R.string.auth_sign_up_error_invalid_email))
                }
            }

            else -> showErrorSnackbar(ex)
        }
    }

    private fun showErrorSnackbar(ex: ApiException) {
        val snackbarData = when (ex) {
            is NetworkException -> StayaSnackbarData.noInternet()
            is ServerUnavailableException -> StayaSnackbarData.serverUnavailable()
            is RateLimitException -> StayaSnackbarData.rateLimited()
            else -> {
                Log.w(TAG, ex)
                StayaSnackbarData.unknown()
            }
        }

        _events.trySend(ForgotPasswordEvent.ShowSnackbar(snackbarData))
    }
}

/**
 * Состояние экрана сброса пароля.
 *
 * @property showPasswordButtonVisible Признак видимости кнопки показа пароля.
 * @property isVerificationCodeBottomSheetVisible Признак видимости шторки ввода кода подтверждения.
 * @property isLoading Флаг выполнения запроса кода.
 *   Не сохраняется при смерти процесса — запрос его не переживает.
 * @property isVerificationLoading Флаг выполнения запроса смены пароля или переотправки кода.
 *   Не сохраняется при смерти процесса — запрос его не переживает.
 * @property minPasswordLength Минимальная длина пароля для подсказки под полями.
 */
@Parcelize
internal data class ForgotPasswordUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val showPasswordButtonVisible: Boolean = false,
    val emailError: StayaString? = null,
    val passwordError: StayaString? = null,
    val repeatPasswordError: StayaString? = null,
    val isVerificationCodeBottomSheetVisible: Boolean = false,
    @IgnoredOnParcel
    val isLoading: Boolean = false,
    @IgnoredOnParcel
    val isVerificationLoading: Boolean = false,
    val minPasswordLength: Int = AuthValidationRules.MIN_PASSWORD_LENGTH,
) : Parcelable

/**
 * Одноразовые события экрана сброса пароля.
 */
internal sealed interface ForgotPasswordEvent {

    /**
     * Сообщение, которое нужно показать пользователю.
     */
    data class ShowSnackbar(val snackbarData: StayaSnackbarData) : ForgotPasswordEvent

    /**
     * Возврат на экран входа: пароль успешно изменён.
     */
    data object NavigateToLogin : ForgotPasswordEvent
}
