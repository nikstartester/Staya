package com.xando.pet_form.presentation

import android.net.Uri
import android.os.Parcelable
import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetSex
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.design.ui.theme.StayaString
import com.xando.pet_form.presentation.utils.latestBirthDate
import com.xando.pet_form.presentation.utils.toBirthDateOrNull
import com.xando.pet_form.presentation.utils.toUtcEpochMillis
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

/**
 * Состояние экрана создания/редактирования питомца.
 *
 * @property petId Идентификатор питомца при редактировании; `null` при создании.
 * @property maxInterests Максимальное количество интересов, которое можно выбрать.
 * @property photoUri Uri локальной копии новой фотографии, выбранной и подтверждённой пользователем.
 * @property cropPhotoUri Копия, для которой сейчас выбирается область обрезки. `null`, если обрезка не идёт.
 * @property existingPhotoUrl URL текущей фотографии питомца при редактировании, пока пользователь не выбрал
 * новую или не убрал фото.
 * @property name Кличка питомца.
 * @property nameError Ошибка поля клички.
 * @property birthDate Дата рождения, как ввёл пользователь: только цифры в порядке ДДММГГГГ.
 * Выбор в календаре приводится к тому же виду.
 * @property birthDateError Ошибка поля даты рождения.
 * @property showDatePicker Признак того, что открыт календарь выбора даты рождения.
 * @property weight Вес питомца в кг, как ввёл пользователь.
 * @property weightError Ошибка поля веса.
 * @property breed Порода питомца.
 * @property breedError Ошибка поля породы.
 * @property sex Пол питомца.
 * @property sexError Ошибка поля пола.
 * @property chosenInterests Выбранные интересы питомца.
 * @property interestChips Интересы, показанные чипами на форме: популярные и следом выбранные вне популярных.
 * @property about Описание питомца.
 * @property isSaving Признак того, что форма отправляется. Не сохраняется при смерти процесса -
 * отправка её не переживает.
 * @property isLoading Признак загрузки формы.
 */
@Parcelize
internal data class PetFormUiState(
    val petId: String?,
    val maxInterests: Int,
    val photoUri: Uri? = null,
    val cropPhotoUri: Uri? = null,
    val existingPhotoUrl: String? = null,
    val name: String = "",
    val nameError: StayaString? = null,
    val birthDate: String = "",
    val birthDateError: StayaString? = null,
    val showDatePicker: Boolean = false,
    val weight: String = "",
    val weightError: StayaString? = null,
    val breed: PetBreed? = null,
    val breedError: StayaString? = null,
    val sex: PetSex? = null,
    val sexError: StayaString? = null,
    val chosenInterests: List<PetInterest> = emptyList(),
    val interestChips: List<PetInterest> = PetInterest.POPULAR,
    val about: String = "",
    @IgnoredOnParcel
    val isSaving: Boolean = false,
    @IgnoredOnParcel
    val isLoading: Boolean = false,
) : Parcelable {

    /** Признак редактирования существующего питомца. */
    val isEditMode: Boolean get() = petId != null

    /**
     * Введённая дата рождения в миллисекундах эпохи (UTC), с которой открывается календарь; `null`,
     * если дата введена не полностью, такой даты нет или она позже [latestBirthDate]: в календаре её
     * всё равно нельзя выбрать.
     */
    val birthDateMillis: Long?
        get() = birthDate.toBirthDateOrNull()?.takeUnless { it.isAfter(latestBirthDate()) }?.toUtcEpochMillis()
}

/**
 * Одноразовые события экрана создания/редактирования питомца.
 */
internal sealed interface PetFormEvent {

    /**
     * Показ snackbar.
     */
    data class ShowSnackbar(val snackbarData: StayaSnackbarData) : PetFormEvent

    /**
     * Возврат на предыдущий экран после успешного сохранения.
     */
    data object NavigateBack : PetFormEvent
}
