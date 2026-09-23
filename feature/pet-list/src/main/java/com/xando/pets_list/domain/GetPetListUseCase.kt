package com.xando.pets_list.domain

import com.xando.core.models.pet.PetSummary
import com.xando.data.pet.PetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Сценарий получения списка питомцев: список из локальной базы и его обновление с сервера.
 *
 * @param petRepository Репозиторий питомцев.
 */
class GetPetListUseCase @Inject constructor(
    private val petRepository: PetRepository
) {
    /**
     * Питомцы текущего пользователя из локальной базы. Актуализируются через [refresh].
     *
     * @return Поток списков кратких данных питомцев.
     */
    operator fun invoke(): Flow<List<PetSummary>> {
        return petRepository.observePets()
    }

    /**
     * Обновляет список питомцев с сервера; новый список придёт через [invoke].
     *
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun refresh() {
        petRepository.refreshPets()
    }
}
