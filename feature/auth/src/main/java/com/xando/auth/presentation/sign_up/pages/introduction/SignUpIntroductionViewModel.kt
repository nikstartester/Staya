package com.xando.auth.presentation.sign_up.pages.introduction

import android.net.Uri
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
 * ViewModel первого шага регистрации.
 */
@HiltViewModel
internal class SignUpIntroductionViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "SignUpIntroductionUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(KEY_STATE, SignUpIntroductionUiState())

    /**@SelfDocumented*/
    val uiState: StateFlow<SignUpIntroductionUiState> = _uiState.asStateFlow()

    private val _events = Channel<SignUpIntroductionEvent>(capacity = Channel.UNLIMITED)

    /**@SelfDocumented*/
    val events: Flow<SignUpIntroductionEvent> = _events.receiveAsFlow()

    /**
     * Обновляет имя и очищает ошибку поля.
     */
    fun updateFirstName(firstName: String) {
        _uiState.update {
            it.copy(
                firstName = firstName,
                firstNameError = null,
            )
        }
    }

    /**
     * Обновляет фамилию и очищает ошибку поля.
     */
    fun updateLastName(lastName: String) {
        _uiState.update {
            it.copy(
                lastName = lastName,
                lastNameError = null,
            )
        }
    }

    /**
     * Обновляет URI выбранной фотографии профиля.
     */
    fun updatePhotoUri(photoUri: Uri) {
        _uiState.update { it.copy(photoUri = photoUri) }
    }

    /**
     * Сохраняет шаг в coordinator и публикует переход к следующему экрану.
     */
    fun onContinueClick() {
        if (!validate()) return

        updateCoordinator()
        _events.trySend(SignUpIntroductionEvent.NavigateNext)
    }

    private fun updateCoordinator() {
        val state = _uiState.value
        coordinator.updateIntroduction(
            firstName = state.firstName,
            lastName = state.lastName,
            photoUri = state.photoUri,
        )
    }

    /**
     * Проверяет обязательные поля первого шага регистрации.
     */
    private fun validate(): Boolean {
        val state = _uiState.value
        val firstNameError = if (state.firstName.isBlank()) "" else null
        val lastNameError = if (state.lastName.isBlank()) "" else null

        _uiState.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
            )
        }

        return firstNameError == null && lastNameError == null
    }
}

/**@SelfDocumented*/
@Parcelize
internal data class SignUpIntroductionUiState(
    val firstName: String = "",
    val lastName: String = "",
    val photoUri: Uri? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
) : Parcelable

/**
 * Одноразовые события первого шага регистрации.
 */
sealed interface SignUpIntroductionEvent {
    data object NavigateNext : SignUpIntroductionEvent
}
