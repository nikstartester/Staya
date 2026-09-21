package com.xando.pet_form.presentation

import android.graphics.RectF
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetDraft
import com.xando.core.models.pet.PetDraftPhoto
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetSex
import com.xando.data.image.PhotoLocalStorage
import com.xando.data.pet.di.PetPhoto
import com.xando.design.ui.snackbar.SnackbarType
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.design.ui.theme.StayaString
import com.xando.feature.pet_form.R
import com.xando.navigation_api.NavigationResultStore
import com.xando.pet_form.domain.GetPetDetailUseCase
import com.xando.pet_form.domain.SavePetUseCase
import com.xando.pet_form.navigation.BREED_PICKER_KEY
import com.xando.pet_form.navigation.INTERESTS_PICKER_KEY
import com.xando.pet_form.presentation.PetFormViewModel.Companion.WEIGHT_INPUT_REGEX
import com.xando.pet_form.presentation.PetFormViewModel.Companion.WEIGHT_PRECISION
import com.xando.pet_form.presentation.utils.BIRTH_DATE_DIGITS
import com.xando.pet_form.presentation.utils.latestBirthDate
import com.xando.pet_form.presentation.utils.toBirthDateInput
import com.xando.pet_form.presentation.utils.toBirthDateOrNull
import com.xando.pet_form.presentation.utils.utcEpochMillisToLocalDate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import com.xando.core.design.R as RDesign

/**
 * ViewModel экрана создания/редактирования питомца.
 *
 * @param petId Идентификатор питомца при редактировании; `null` при создании.
 */
@HiltViewModel(assistedFactory = PetFormViewModel.Factory::class)
internal class PetFormViewModel @AssistedInject constructor(
    @Assisted petId: String?,
    private val savePetUseCase: SavePetUseCase,
    private val getPetDetailUseCase: GetPetDetailUseCase,
    @param:PetPhoto private val photoLocalStorage: PhotoLocalStorage,
    private val navigationResultStore: NavigationResultStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private companion object {
        const val TAG = "PetFormViewModel"
        const val KEY_STATE = "PetFormUiState"

        const val KEY_PET_DETAILS_LOADED = "PetDetailsLoaded"

        const val WEIGHT_PRECISION = 3

        /** Допустимый ввод веса: цифры, не более одного разделителя (точка или запятая) и до 3 цифр после него. */
        val WEIGHT_INPUT_REGEX = Regex("^\\d*([.,]\\d{0,$WEIGHT_PRECISION})?$")

        /** Максимальное количество интересов, которое можно выбрать для питомца. */
        private const val MAX_INTERESTS = 6

        private const val GRAMS_IN_KG = 1000
    }

    /**@SelfDocumented*/
    @AssistedFactory
    interface Factory {
        /**@SelfDocumented*/
        fun create(petId: String?): PetFormViewModel
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        PetFormUiState(petId = petId, maxInterests = MAX_INTERESTS)
    )

    /**
     * Состояние экрана создания/редактирования питомца.
     */
    val uiState: StateFlow<PetFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<PetFormEvent>(capacity = Channel.UNLIMITED)

    private val petDetailsLoaded = savedStateHandle.getMutableStateFlow(
        KEY_PET_DETAILS_LOADED,
        false
    )

    /**
     * Одноразовые события экрана создания/редактирования питомца.
     */
    val events: Flow<PetFormEvent> = _events.receiveAsFlow()

    init {
        if (petId != null && !petDetailsLoaded.value) loadPetDetail(petId)

        viewModelScope.launch {
            navigationResultStore.results<PetBreed>(BREED_PICKER_KEY).collect { breed ->
                _uiState.update { it.copy(breed = breed, breedError = null) }
            }
        }
        viewModelScope.launch {
            navigationResultStore.results<List<PetInterest>>(INTERESTS_PICKER_KEY)
                .collect { chosenInterests ->
                    _uiState.update { it.withChosenInterests(chosenInterests) }
                }
        }
    }

    private fun loadPetDetail(petId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val detail = getPetDetailUseCase(petId)
                _uiState.update {
                    it.copy(
                        name = detail.name,
                        birthDate = detail.birthDate.isoDateToBirthDateInput(),
                        weight = detail.weightGrams.toKgInput(),
                        breed = detail.breed,
                        sex = detail.sex,
                        about = detail.description.orEmpty(),
                        existingPhotoUrl = detail.photoUrl,
                        isLoading = false
                    ).withChosenInterests(detail.interests)
                }
                petDetailsLoaded.update { true }
            } catch (th: Throwable) {
                ensureActive()
                Log.w(TAG, th)
                _events.trySend(PetFormEvent.ShowSnackbar(StayaSnackbarData.unknown()))
            }
        }
    }

    /**
     * Обновляет кличку и очищает ошибку поля.
     */
    fun updateName(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    /**
     * Обновляет дату рождения, введённую с клавиатуры, и очищает ошибку поля. Из [input] берутся только
     * цифры, поэтому вставка «12.09.2022» тоже работает; ввод длиннее [com.xando.pet_form.presentation.utils.BIRTH_DATE_DIGITS] цифр игнорируется.
     */
    fun updateBirthDate(input: String) {
        val digits = input.filter(Char::isDigit)
        if (digits.length > BIRTH_DATE_DIGITS) return

        _uiState.update { it.copy(birthDate = digits, birthDateError = null) }
    }

    /**
     * Открывает календарь выбора даты рождения.
     */
    fun openDatePicker() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    /**
     * Закрывает календарь выбора даты рождения без изменений.
     */
    fun dismissDatePicker() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    /**
     * Подтверждает дату рождения, выбранную в календаре: она попадает в поле в том же виде, что и
     * ввод с клавиатуры.
     */
    fun selectBirthDate(millis: Long) {
        val birthDate = millis.utcEpochMillisToLocalDate().toBirthDateInput()
        _uiState.update { it.copy(birthDate = birthDate, birthDateError = null, showDatePicker = false) }
    }

    /**
     * Обновляет вес и очищает ошибку поля.
     */
    fun updateWeight(weight: String) {
        if (!WEIGHT_INPUT_REGEX.matches(weight)) return

        _uiState.update { it.copy(weight = weight, weightError = null) }
    }

    /**
     * Выбирает пол питомца.
     */
    fun selectSex(sex: PetSex) {
        _uiState.update { it.copy(sex = sex, sexError = null) }
    }

    /**
     * Переключает интерес [interest], показанный чипом на форме. Если интерес ещё не выбран и лимит
     * [MAX_INTERESTS] уже достигнут, ничего не делает. Снятый интерес вне популярных с формы
     * пропадает: вернуть его можно через экран выбора интересов.
     */
    fun toggleInterest(interest: PetInterest) {
        _uiState.update { state ->
            val interests = when {
                interest in state.chosenInterests -> state.chosenInterests - interest
                state.chosenInterests.size >= MAX_INTERESTS -> return@update state
                else -> state.chosenInterests + interest
            }
            state.withChosenInterests(interests)
        }
    }

    private fun PetFormUiState.withChosenInterests(chosen: List<PetInterest>): PetFormUiState = copy(
        chosenInterests = chosen,
        interestChips = PetInterest.POPULAR + chosen.filterNot { it in PetInterest.POPULAR },
    )

    /**
     * Обновляет описание питомца.
     */
    fun updateAbout(about: String) {
        _uiState.update { it.copy(about = about) }
    }

    /**
     * Принимает выбранную в пикере фотографию и открывает выбор области обрезки.
     */
    fun onPhotoPicked(photoUri: Uri) {
        viewModelScope.launch {
            val cachedPhotoUri = try {
                photoLocalStorage.save(photoUri)
            } catch (th: Throwable) {
                ensureActive()
                Log.w(TAG, "Не удалось скопировать фото в кеш", th)
                _events.trySend(PetFormEvent.ShowSnackbar(errorSnackbar(R.string.pet_form_photo_unsupported_format)))
                return@launch
            }

            _uiState.update { it.copy(cropPhotoUri = cachedPhotoUri) }
        }
    }

    /**
     * Обрезает фотографию по выбранной области и делает её фотографией питомца.
     */
    fun onCropConfirmed(cropRect: RectF) {
        val sourceUri = _uiState.value.cropPhotoUri ?: return
        _uiState.update { it.copy(cropPhotoUri = null) }

        viewModelScope.launch {
            val croppedPhotoUri = try {
                photoLocalStorage.cut(sourceUri, cropRect).also { photoLocalStorage.keepOnly(it) }
            } catch (th: Throwable) {
                ensureActive()
                Log.w(TAG, "Не удалось обрезать фото", th)
                photoLocalStorage.delete(sourceUri)
                _events.trySend(PetFormEvent.ShowSnackbar(errorSnackbar(R.string.pet_form_photo_crop_error)))
                return@launch
            }

            _uiState.update { it.copy(photoUri = croppedPhotoUri, existingPhotoUrl = null) }
        }
    }

    /**
     * Отменяет обрезку: копия удаляется, а фотографией остаётся прежняя.
     */
    fun onCropDismissed() {
        val sourceUri = _uiState.value.cropPhotoUri ?: return
        _uiState.update { it.copy(cropPhotoUri = null) }

        viewModelScope.launch { photoLocalStorage.delete(sourceUri) }
    }

    /**
     * Убирает фотографию с формы: локальная копия новой фотографии удаляется, а фотография с сервера
     * будет удалена при сохранении.
     */
    fun removePhoto() {
        val localPhotoUri = _uiState.value.photoUri
        _uiState.update { it.copy(photoUri = null, existingPhotoUrl = null) }

        if (localPhotoUri != null) viewModelScope.launch { photoLocalStorage.delete(localPhotoUri) }
    }

    /**
     * Проверяет обязательные поля и сохраняет питомца.
     */
    fun save() {
        val state = _uiState.value
        if (state.isSaving) return

        val draft = state.toDraftOrShowErrors() ?: return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            try {
                savePetUseCase(draft)
                _events.trySend(PetFormEvent.NavigateBack)
            } catch (ex: ApiException) {
                Log.w(TAG, "Не удалось сохранить питомца", ex)
                val message = when (ex) {
                    is NetworkException -> StayaSnackbarData.noInternet()
                    is ServerUnavailableException -> StayaSnackbarData.serverUnavailable()
                    else -> errorSnackbar(R.string.pet_form_save_error)
                }
                _events.trySend(PetFormEvent.ShowSnackbar(message))
            } catch (th: Throwable) {
                ensureActive()
                Log.w(TAG, th)
                _events.trySend(PetFormEvent.ShowSnackbar(StayaSnackbarData.unknown()))
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    /**
     * Убирает локальные копии фото: после ухода с формы они не нужны, отправленная копия уже на сервере.
     */
    override fun onCleared() {
        photoLocalStorage.clear()
    }

    /**
     * Собирает черновик из заполненной формы либо, если обязательное поле не заполнено или заполнено
     * неверно, подсвечивает такие поля и возвращает `null`.
     */
    private fun PetFormUiState.toDraftOrShowErrors(): PetDraft? {
        val weightGrams = weight.toWeightGramsOrNull()
        val parsedBirthDate = birthDate.toBirthDateOrNull()
        val birthDateError = birthDateError(parsedBirthDate)
        val validBirthDate = parsedBirthDate.takeIf { birthDateError == null }
        val breed = breed
        val sex = sex

        if (name.isBlank() || validBirthDate == null || weightGrams == null || breed == null || sex == null) {
            _uiState.update { state ->
                state.copy(
                    nameError = StayaString.EMPTY.takeIf { name.isBlank() },
                    birthDateError = birthDateError,
                    weightError = StayaString.EMPTY.takeIf { weightGrams == null },
                    breedError = StayaString.EMPTY.takeIf { breed == null },
                    sexError = StayaString.EMPTY.takeIf { sex == null },
                )
            }
            return null
        }

        return PetDraft(
            petId = petId,
            name = name.trim(),
            birthDate = validBirthDate.toString(),
            breed = breed,
            sex = sex,
            weightGrams = weightGrams,
            interests = chosenInterests,
            description = about.trim().ifBlank { null },
            // Удаление уходит и когда фото на сервере не было: форма этого не хранит, а серверу такой
            // запрос ничего не стоит. В целом, можно оставить и так.
            photo = when {
                photoUri != null -> PetDraftPhoto.Replace(photoUri.toString())
                isEditMode && existingPhotoUrl == null -> PetDraftPhoto.Remove
                else -> PetDraftPhoto.Keep
            },
        )
    }

    /**
     * Ошибка поля даты рождения по введённому [birthDate][PetFormUiState.birthDate] и результату его
     * разбора [parsedBirthDate]: подсветка без текста для пустого поля, текст для неполной или
     * несуществующей даты и для даты позже [com.xando.pet_form.presentation.utils.latestBirthDate]; `null`, если дата в порядке.
     */
    private fun PetFormUiState.birthDateError(parsedBirthDate: LocalDate?): StayaString? = when {
        parsedBirthDate == null && birthDate.isEmpty() -> StayaString.EMPTY
        parsedBirthDate == null -> StayaString.Res(R.string.pet_form_birth_date_invalid)
        parsedBirthDate.isAfter(latestBirthDate()) -> StayaString.Res(R.string.pet_form_birth_date_future)
        else -> null
    }

    private fun errorSnackbar(@StringRes messageResId: Int) = StayaSnackbarData(
        type = SnackbarType.ERROR,
        messageResId = messageResId,
        iconRes = RDesign.drawable.design_ic_error_24px,
    )

    /** Дата в формате ISO (`2022-09-12`) как содержимое поля даты рождения; пустая строка, если дата не разбирается. */
    private fun String.isoDateToBirthDateInput(): String =
        runCatching { LocalDate.parse(this) }.getOrNull()?.toBirthDateInput().orEmpty()

    /** Вес в граммах как строка в килограммах: целое - без дробной части, дробное - с [WEIGHT_PRECISION] знаками. */
    private fun Int.toKgInput(): String {
        val precisionValue = 10.0.pow(WEIGHT_PRECISION)
        val kg = (this * precisionValue / GRAMS_IN_KG).roundToLong() / precisionValue
        return if (kg == kg.toLong().toDouble()) kg.toLong().toString() else kg.toString()
    }

    /** Введённый пользователем вес в килограммах как вес в граммах; `null`, если ввод не вес. */
    private fun String.toWeightGramsOrNull(): Int? {
        val kg = replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() } ?: return null

        return (kg * GRAMS_IN_KG).roundToInt().takeIf { it > 0 }
    }
}
