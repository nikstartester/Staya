package com.xando.auth.sign_up

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.auth.sign_up.domain.SignUpInteractor
import com.xando.auth.verification_code.VerificationCodeFeedback
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.ConflictException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.RateLimitException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.core.api_models.ValidationException
import com.xando.core.models.auth.data.SignUpData
import com.xando.data.auth.AuthConflictCodes
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
 * ViewModel верхнего уровня для всего signup-флоу.
 */
@HiltViewModel
internal class SignUpScreenViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    private val serverFeedback: SignUpFlowFeedback,
    private val verificationFeedback: VerificationCodeFeedback,
    private val savedStateHandle: SavedStateHandle,
    private val interactor: SignUpInteractor
) : ViewModel() {

    private companion object {
        const val TAG = "SignUpScreenViewModel"

        const val KEY_FLOW_DATA = "SignUpScreenFlowData"
        const val KEY_STATE = "SignUpScreenUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        SignUpScreenUiState()
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<SignUpScreenUiState> = _uiState.asStateFlow()

    private val _events = Channel<SignUpFlowEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpFlowEvent> = _events.receiveAsFlow()

    init {
        serverFeedback.clear()
        verificationFeedback.clear()

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
     * Завершает флоу регистрации и отправляет собранные данные на сервер.
     */
    fun onFinalStepCompleted() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }

        val flowData = coordinator.getSnapshot()
        savedStateHandle[KEY_FLOW_DATA] = flowData

        viewModelScope.launch {
            try {
                interactor.signUp(flowData.toSignUpData())
                showVerificationCodeBottomSheet()
            } catch (ex: ApiException) {
                catchSignUpApiException(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(SignUpFlowEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                // TODO: перейти на Timber
                Log.w(TAG, th)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Обрабатывает нажатие на крестик, когда флоу уже начат: показывает диалог подтверждения выхода.
     */
    fun onCloseClick() {
        _uiState.update { it.copy(isExitConfirmationVisible = true) }
    }

    /**@SelfDocumented*/
    fun hideExitConfirmation() {
        _uiState.update { it.copy(isExitConfirmationVisible = false) }
    }

    /**@SelfDocumented*/
    fun hideVerificationCodeBottomSheet() {
        _uiState.update { it.copy(isVerificationCodeBottomSheetVisible = false) }
    }

    /**
     * Подтверждает e-mail введённым кодом. При успехе сохраняются токены, и приложение уходит из auth-флоу.
     */
    fun onVerificationCodeConfirmed(code: String) {
        if (_uiState.value.isVerificationLoading) return

        _uiState.update { it.copy(isVerificationLoading = true) }

        val flowData = coordinator.getSnapshot()
        viewModelScope.launch {
            try {
                interactor.verifyEmail(email = flowData.email, code = code)
                flowData.photoUri?.let { interactor.uploadAvatar(it) }
                hideVerificationCodeBottomSheet()
            } catch (ex: ApiException) {
                catchVerifyEmailApiException(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(SignUpFlowEvent.ShowSnackbar(StayaSnackbarData.unknown()))
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

        val email = coordinator.getSnapshot().email
        viewModelScope.launch {
            try {
                interactor.resendCode(email)
            } catch (ex: ApiException) {
                showErrorSnackbar(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(SignUpFlowEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                // TODO: перейти на Timber
                Log.w(TAG, th)
            } finally {
                _uiState.update { it.copy(isVerificationLoading = false) }
            }
        }
    }

    private fun showVerificationCodeBottomSheet() {
        _uiState.update {
            it.copy(isVerificationCodeBottomSheetVisible = true)
        }
    }

    private fun catchSignUpApiException(ex: ApiException) {
        if (ex is ValidationException && applySignUpFieldError(ex.code)) return

        if (ex is ConflictException && ex.code == AuthConflictCodes.EMAIL_ALREADY_EXISTS) {
            serverFeedback.sendEmailPasswordErrors(
                emailError = StayaString.Res(R.string.auth_sign_up_error_email_exists),
                passwordError = null
            )
        }

        if (ex is ConflictException && ex.code == AuthConflictCodes.LOGIN_ALREADY_EXISTS) {
            serverFeedback.sendLoginError(StayaString.Res(R.string.auth_sign_up_error_login_exists))
            _events.trySend(SignUpFlowEvent.NavigateToLoginStep)
        }

        showErrorSnackbar(ex)
    }

    /**
     * Показывает ошибку валидации под соответствующим полем.
     *
     * @param code Код ошибки с сервера.
     * @return `true`, если код удалось разложить по полям формы.
     */
    private fun applySignUpFieldError(code: String): Boolean {
        when (code) {
            AuthValidationCodes.INVALID_EMAIL -> serverFeedback.sendEmailPasswordErrors(
                emailError = StayaString.Res(R.string.auth_sign_up_error_invalid_email),
                passwordError = null
            )

            AuthValidationCodes.WEAK_PASSWORD -> serverFeedback.sendEmailPasswordErrors(
                emailError = null,
                passwordError = StayaString.Res(R.string.auth_sign_up_error_weak_password)
            )

            else -> return false
        }

        return true
    }

    private fun catchVerifyEmailApiException(ex: ApiException) {
        val code = (ex as? ValidationException)?.code

        val errorResId = when (code) {
            INVALID_CODE -> R.string.auth_verification_code_error_invalid_code
            CODE_NOT_FOUND -> R.string.auth_verification_code_error_not_found
            CODE_EXPIRED -> R.string.auth_verification_code_error_expired
            CODE_MAX_ATTEMPTS -> R.string.auth_verification_code_error_max_attempts
            else -> null
        }

        if (errorResId == null) {
            showErrorSnackbar(ex)
            return
        }

        // Досрочно разблокируем повторную отправку, только если код стал непригоден сам по себе.
        // При CODE_MAX_ATTEMPTS лимит исчерпан попытками пользователя — отсчёт дожидается конца.
        val isResendAllowed = code == CODE_NOT_FOUND || code == CODE_EXPIRED

        verificationFeedback.sendError(
            error = StayaString.Res(errorResId),
            isResendAllowed = isResendAllowed
        )
    }

    private fun showErrorSnackbar(ex: ApiException) {
        val snackbarData = when (ex) {
            is ConflictException if ex.code == AuthConflictCodes.EMAIL_ALREADY_EXISTS -> StayaSnackbarData(
                type = SnackbarType.ERROR,
                messageResId = R.string.auth_sign_up_error_email_exists,
                iconRes = RDesign.drawable.design_ic_mail_24dp
            )

            is ConflictException if ex.code == AuthConflictCodes.LOGIN_ALREADY_EXISTS -> StayaSnackbarData(
                type = SnackbarType.ERROR,
                messageResId = R.string.auth_sign_up_error_login_exists,
                iconRes = RDesign.drawable.design_ic_error_24px
            )

            is NetworkException -> StayaSnackbarData.noInternet()
            is ServerUnavailableException -> StayaSnackbarData.serverUnavailable()
            is RateLimitException -> StayaSnackbarData.rateLimited()
            else -> {
                // TODO: перейти на Timber
                Log.w(TAG, ex)
                StayaSnackbarData.unknown()
            }
        }

        _events.trySend(SignUpFlowEvent.ShowSnackbar(snackbarData))
    }
}

private fun SignUpFlowData.toSignUpData() = SignUpData(
    firstName = firstName,
    lastName = lastName,
    description = description,
    login = login,
    email = email,
    password = password,
)

/**
 * Состояние экрана регистрации.
 *
 * @property isLoading Флаг выполнения запроса регистрации.
 *   Не сохраняется при смерти процесса — запрос его не переживает.
 * @property isVerificationCodeBottomSheetVisible Признак видимости шторки ввода кода подтверждения.
 * @property isVerificationLoading Флаг выполнения запроса подтверждения или переотправки кода.
 *   Не сохраняется при смерти процесса — запрос его не переживает.
 * @property isExitConfirmationVisible Признак видимости диалога подтверждения выхода из флоу регистрации.
 */
@Parcelize
internal data class SignUpScreenUiState(
    @IgnoredOnParcel
    val isLoading: Boolean = false,
    val isVerificationCodeBottomSheetVisible: Boolean = false,
    @IgnoredOnParcel
    val isVerificationLoading: Boolean = false,
    val isExitConfirmationVisible: Boolean = false,
) : Parcelable

/**
 * Одноразовые события верхнего уровня для всего signup-флоу.
 */
internal sealed interface SignUpFlowEvent {

    /**
     * Ошибка регистрации, которую нужно показать пользователю.
     */
    data class ShowSnackbar(val snackbarData: StayaSnackbarData) : SignUpFlowEvent

    /**
     * Возврат на шаг ввода логина: сервер сообщил, что логин уже занят.
     */
    data object NavigateToLoginStep : SignUpFlowEvent
}
