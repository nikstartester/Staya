package com.xando.pet_form.domain

import com.xando.core.models.pet.PetDraft
import com.xando.data.pet.PetRepository
import javax.inject.Inject

/**
 * Сценарий сохранения питомца: создание нового или обновление существующего - что именно, определяет
 * [PetDraft.petId].
 *
 * @param petRepository Репозиторий питомцев.
 */
internal class SavePetUseCase @Inject constructor(private val petRepository: PetRepository) {
    /**
     * Сохраняет питомца по [draft].
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend operator fun invoke(draft: PetDraft) {
        petRepository.savePet(draft)
    }
}
