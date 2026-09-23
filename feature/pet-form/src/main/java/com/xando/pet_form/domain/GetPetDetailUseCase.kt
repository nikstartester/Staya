package com.xando.pet_form.domain

import com.xando.core.models.pet.PetDetail
import com.xando.data.pet.PetRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Сценарий получения полной информации о питомце для редактирования.
 *
 * @param petRepository Репозиторий питомцев.
 */
internal class GetPetDetailUseCase @Inject constructor(private val petRepository: PetRepository) {

    /**
     * Получает полную информацию о питомце: из локальной базы, а если питомца там нет - с сервера.
     *
     * @param petId Идентификатор питомца.
     * @throws com.xando.core.api_models.ApiException Если питомца нет в базе, а загрузить его не удалось.
     * @throws IllegalStateException Если питомца не удалось прочитать из базы и после загрузки.
     */
    suspend operator fun invoke(petId: String): PetDetail {
        petRepository.observePet(petId).first()?.let { return it }

        petRepository.refreshPet(petId)
        return petRepository.observePet(petId).first() ?: error("Питомец $petId не прочитан из базы")
    }
}
