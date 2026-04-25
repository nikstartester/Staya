package com.xando.auth.presentation.login

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.auth.domain.LoginUseCase
import com.xando.auth.domain.validation.AuthValidationRules.MIN_PASSWORD_LENGTH
import com.xando.auth.domain.validation.LoginValidationError.LoginEmpty
import com.xando.auth.domain.validation.LoginValidationError.PasswordTooShort
import com.xando.auth.domain.validation.LoginValidationException
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.ForbiddenException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.RateLimitException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.core.api_models.UnauthorizedException
import com.xando.design.ui.snackbar.SnackbarType
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.design.ui.theme.StayaString
import com.xando.feature.auth.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.xando.core.design.R as RDesign

/**
 * ViewModel экрана входа.
 *
 * Отвечает за обработку пользовательского ввода, валидацию данных
 * и управление состоянием [LoginUiState].
 */
@HiltViewModel
internal class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCase: LoginUseCase
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "LoginUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        LoginUiState()
    )

    /**@SelfDocumented*/
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val _events = Channel<LoginEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    /**
     * Обновляет email в текущих данных для входа.
     */
    fun onEmailChanged(email: String) {
        updateCredentials { it.copy(login = email) }
        _uiState.update { it.copy(loginError = null) }
    }

    /**
     * Обновляет пароль в текущих данных для входа.
     */
    fun onPasswordChanged(password: String) {
        updateCredentials { it.copy(password = password) }
        _uiState.update { it.copy(passwordError = null) }
    }

    /**
     * Запускает процесс авторизации с текущими данными.
     */
    fun onLoginClick() {
        val current = _uiState.value
        if (current.isLoading) return

        _uiState.update { it.copy(isLoading = true) }

        val credentials = _uiState.value.loginCredentials
        viewModelScope.launch {
            try {
                useCase.login(credentials.login, credentials.password)
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: LoginValidationException) {
                catchValidationException(ex)
            } catch (ex: ApiException) {
                catchApiException(ex)
            } catch (th: Throwable) {
                _events.trySend(LoginEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                // TODO: перейти на Timber
                Log.w("LoginViewModel", th)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Обновляет данные для входа и пересчитывает UI-зависимые флаги.
     */
    private fun updateCredentials(
        transform: (LoginCredentials) -> LoginCredentials
    ) {
        _uiState.update { state ->
            if (state.isLoading) {
                state
            } else {
                val updated = transform(state.loginCredentials)
                state.copy(loginCredentials = updated)
            }
        }
    }

    private fun catchValidationException(ex: LoginValidationException) {
        var loginError: StayaString? = null
        var passwordError: StayaString? = null
        ex.errors.forEach {
            when (it) {
                LoginEmpty -> loginError = StayaString.EMPTY
                PasswordTooShort -> {
                    passwordError = StayaString.Res(
                        R.string.auth_sign_up_email_password_hint,
                        arrayOf(MIN_PASSWORD_LENGTH)
                    )
                }
            }
        }

        _uiState.update {
            it.copy(loginError = loginError, passwordError = passwordError)
        }
    }

    private fun catchApiException(ex: ApiException) {
        val message =
            when (ex) {
                is UnauthorizedException -> {
                    StayaSnackbarData(
                        SnackbarType.ERROR,
                        messageResId = R.string.auth_login_error_invalid_credentials,
                        iconRes = RDesign.drawable.design_ic_lock_24dp
                    )
                }

                is ForbiddenException -> {
                    StayaSnackbarData(
                        SnackbarType.ERROR,
                        messageResId = R.string.auth_login_error_account_banned,
                        iconRes = RDesign.drawable.design_ic_account_circle_off_24px
                    )
                }

                is NetworkException -> StayaSnackbarData.noInternet()
                is ServerUnavailableException -> StayaSnackbarData.serverUnavailable()
                is RateLimitException -> StayaSnackbarData.rateLimited()

                else -> {
                    // TODO: перейти на Timber
                    Log.w("LoginViewModel", ex)
                    StayaSnackbarData.unknown()
                }
            }
        _events.trySend(LoginEvent.ShowSnackbar(message))
    }
}
