package com.xando.auth.sign_up.pages.introduction

import android.graphics.RectF
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.auth.sign_up.SignUpFlowCoordinator
import com.xando.auth.sign_up.di.SignUpAvatar
import com.xando.domain.image.PhotoUploadUseCase
import com.xando.design.ui.snackbar.SnackbarType
import com.xando.design.ui.snackbar.StayaSnackbarData
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
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import com.xando.core.design.R as RDesign

/**
 * ViewModel первого шага регистрации.
 */
@HiltViewModel
internal class SignUpIntroductionViewModel @Inject constructor(
    private val coordinator: SignUpFlowCoordinator,
    @param:SignUpAvatar private val photoUploadUseCase: PhotoUploadUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val TAG = "SignUpIntroductionViewModel"

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
     * Принимает выбранную в пикере фотографию и открывает выбор области обрезки.
     *
     * Фотография копируется в кеш приложения: грант пикера временный и не переживает смерть процесса,
     * а отправляется фото сильно позже — уже после подтверждения e-mail. Область обрезки выбирается
     * по этой копии: она развёрнута по EXIF и уменьшена, поэтому пользователь выбирает область на
     * тех же пикселях, которые в итоге и обрежутся.
     *
     * Фотографией профиля копия становится только после подтверждения обрезки — до этого момента
     * выбранной остаётся прежняя фотография.
     *
     * Если формат не поддерживается или копия не сохранилась, выбор не применяется и показывается снекбар.
     */
    fun onPhotoPicked(photoUri: Uri) {
        viewModelScope.launch {
            val cachedPhotoUri = try {
                photoUploadUseCase.cachePhoto(photoUri)
            } catch (ex: CancellationException) {
                throw ex
            } catch (th: Throwable) {
                // TODO: перейти на Timber
                Log.w(TAG, "Не удалось скопировать фото в кеш", th)
                _events.trySend(SignUpIntroductionEvent.ShowSnackbar(unsupportedPhotoFormat()))
                return@launch
            }

            _uiState.update { it.copy(cropPhotoUri = cachedPhotoUri) }
        }
    }

    /**
     * Обрезает фотографию по выбранной области и делает её фотографией профиля.
     *
     * Прежняя фотография вытесняется только здесь — в точке подтверждения выбора.
     *
     * @param cropRect Область в долях сторон копии: границы лежат в 0..1, а не в пикселях.
     */
    fun onCropConfirmed(cropRect: RectF) {
        val sourceUri = _uiState.value.cropPhotoUri ?: return
        _uiState.update { it.copy(cropPhotoUri = null) }

        viewModelScope.launch {
            val croppedPhotoUri = try {
                photoUploadUseCase.cutPhoto(sourceUri, cropRect).also { photoUploadUseCase.confirmPhoto(it) }
            } catch (ex: CancellationException) {
                throw ex
            } catch (th: Throwable) {
                // TODO: перейти на Timber
                Log.w(TAG, "Не удалось обрезать фото", th)
                photoUploadUseCase.discardPhoto(sourceUri)
                _events.trySend(SignUpIntroductionEvent.ShowSnackbar(photoCropFailed()))
                return@launch
            }

            _uiState.update { it.copy(photoUri = croppedPhotoUri) }
        }
    }

    /**
     * Отменяет обрезку: копия удаляется, а выбранной остаётся прежняя фотография.
     */
    fun onCropDismissed() {
        val sourceUri = _uiState.value.cropPhotoUri ?: return
        _uiState.update { it.copy(cropPhotoUri = null) }

        viewModelScope.launch { photoUploadUseCase.discardPhoto(sourceUri) }
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

    /**@SelfDocumented*/
    private fun unsupportedPhotoFormat() = StayaSnackbarData(
        type = SnackbarType.ERROR,
        messageResId = R.string.auth_sign_up_introduction_photo_unsupported_format,
        iconRes = RDesign.drawable.design_ic_error_24px,
    )

    /**@SelfDocumented*/
    private fun photoCropFailed() = StayaSnackbarData(
        type = SnackbarType.ERROR,
        messageResId = R.string.auth_sign_up_introduction_photo_crop_error,
        iconRes = RDesign.drawable.design_ic_error_24px,
    )
}

/**@SelfDocumented*/
@Parcelize
internal data class SignUpIntroductionUiState(
    val firstName: String = "",
    val lastName: String = "",
    val photoUri: Uri? = null,
    /** Копия, для которой сейчас выбирается область обрезки. `null`, если обрезка не идёт. */
    val cropPhotoUri: Uri? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
) : Parcelable

/**
 * Одноразовые события первого шага регистрации.
 */
sealed interface SignUpIntroductionEvent {
    data object NavigateNext : SignUpIntroductionEvent

    /**
     * Ошибка, которую нужно показать пользователю.
     */
    data class ShowSnackbar(val snackbarData: StayaSnackbarData) : SignUpIntroductionEvent
}
