package com.xando.data.pet

import android.content.Context
import androidx.core.net.toUri
import com.xando.core.models.pet.PetDetail
import com.xando.core.models.pet.PetDraft
import com.xando.core.models.pet.PetDraftPhoto
import com.xando.core.models.pet.PetSummary
import com.xando.data.pet.model.mapToDetail
import com.xando.data.pet.model.mapToSummary
import com.xando.data.pet.model.toRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Репозиторий данных о питомцах.
 *
 * @param remoteDataSource Запросы к серверу, связанные с питомцами.
 * @param context Контекст приложения для чтения выбранного фото по Uri.
 */
class PetRepository @Inject internal constructor(
    private val remoteDataSource: PetRemoteDataSource,
    @param:ApplicationContext private val context: Context,
) {

    /**
     * Получает список питомцев.
     *
     * @return Список кратких данных питомцев.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun getPetList(): List<PetSummary> {
        return remoteDataSource.getPets().map { it.mapToSummary() }
    }

    /**
     * Получает полную информацию о питомце.
     *
     * @param petId Идентификатор питомца.
     * @return Полная информация о питомце.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun getPetDetail(petId: String): PetDetail {
        return remoteDataSource.getPet(petId).pet.mapToDetail()
    }

    /**
     * Сохраняет питомца на сервере: создаёт нового или обновляет существующего с [PetDraft.petId].
     * Если [PetDraft.photo] требует новое фото, сначала загружает его; удаление текущего фото сервер выполняет сам.
     *
     * @param draft Черновик формы: что сохранить и что сделать с фото.
     * @return Сохранённый питомец в том виде, в каком его вернул сервер.
     * @throws IllegalStateException Если новое фото не удалось прочитать по его Uri.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun savePet(draft: PetDraft): PetDetail {
        val photoBytes = (draft.photo as? PetDraftPhoto.Replace)?.let { readPhoto(it.localUri) }
        val photoFileId = photoBytes?.let { remoteDataSource.uploadPhoto(it) }
        val request = draft.toRequest(photoFileId)

        val petId = draft.petId
        val response = if (petId == null) {
            remoteDataSource.createPet(request)
        } else {
            remoteDataSource.updatePet(petId, request)
        }

        return response.pet.mapToDetail()
    }

    /**
     * Читает файл фото по [uriString].
     *
     * @throws IllegalStateException Если файл по [uriString] не удалось прочитать.
     */
    private suspend fun readPhoto(uriString: String): ByteArray {
        // TODO: принимать извне. См. todo в PetPhotoModule
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uriString.toUri())?.use { it.readBytes() }
        } ?: error("Не удалось прочитать файл по uri: $uriString")
    }
}
