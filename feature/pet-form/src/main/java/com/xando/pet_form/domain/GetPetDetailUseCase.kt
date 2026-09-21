package com.xando.pet_form.domain

import com.xando.core.models.pet.PetDetail
import com.xando.data.pet.PetRepository
import javax.inject.Inject

/**
 * Сценарий получения полной информации о питомце для редактирования.
 *
 * @param petRepository Репозиторий питомцев.
 */
class GetPetDetailUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    /**
     * Получает полную информацию о питомце.
     *
     * @param petId Идентификатор питомца.
     */
    suspend operator fun invoke(petId: String): PetDetail {
        return petRepository.getPetDetail(petId)
    }
}
