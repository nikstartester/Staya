package com.xando.pets_list.domain

import com.xando.core.models.pet.PetSummary
import com.xando.data.pet_list.PetListRepository
import javax.inject.Inject

/**
 * Сценарий получения списка питомцев.
 *
 * @param petListRepository Репозиторий списка питомцев.
 */
class GetPetListUseCase @Inject constructor(
    private val petListRepository: PetListRepository
) {
    /**
     * Получает список питомцев.
     *
     * @return Список кратких данных питомцев.
     */
    suspend operator fun invoke(): List<PetSummary> {
        return petListRepository.getPetList()
    }
}
