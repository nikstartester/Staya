package com.xando.core.models.pet

/**
 * Черновик формы питомца: то, что ввёл пользователь, но ещё не отправлено на сервер.
 *
 * @property petId Идентификатор питомца при редактировании; `null` при создании.
 * @property name Кличка питомца.
 * @property birthDate Дата рождения в формате "yyyy-MM-dd".
 * @property breed Порода питомца.
 * @property sex Пол питомца.
 * @property weightGrams Вес питомца в граммах.
 * @property interests Список интересов питомца.
 * @property description Описание питомца.
 * @property photo Что сделать с фотографией питомца при сохранении.
 */
data class PetDraft(
    val petId: String?,
    val name: String,
    val birthDate: String,
    val breed: PetBreed,
    val sex: PetSex,
    val weightGrams: Int,
    val interests: List<PetInterest>,
    val description: String?,
    val photo: PetDraftPhoto,
)

/**
 * Что сделать с фотографией питомца при сохранении [PetDraft].
 */
sealed interface PetDraftPhoto {

    /** Оставить фотографию, которая уже есть на сервере, или не добавлять никакой. */
    data object Keep : PetDraftPhoto

    /** Убрать фотографию с сервера. */
    data object Remove : PetDraftPhoto

    /**
     * Загрузить новую фотографию вместо текущей.
     *
     * @property localUri Uri локальной копии выбранного фото.
     */
    data class Replace(val localUri: String) : PetDraftPhoto
}
