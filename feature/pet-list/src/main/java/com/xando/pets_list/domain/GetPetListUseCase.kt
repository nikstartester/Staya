package com.xando.pets_list.domain

import com.xando.core.models.pet.PetSummary
import com.xando.data.pet.PetRepository
import javax.inject.Inject

/**
 * Сценарий получения списка питомцев.
 *
 * @param petRepository Репозиторий питомцев.
 */
class GetPetListUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    /**
     * Получает список питомцев.
     *
     * @return Список кратких данных питомцев.
     */
    suspend operator fun invoke(): List<PetSummary> {
        return petRepository.getPetList()
    }
}
