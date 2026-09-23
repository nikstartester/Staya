package com.xando.data.pet

import android.content.Context
import androidx.core.net.toUri
import com.xando.core.api_models.NotFoundException
import com.xando.core.database.pet.PetDao
import com.xando.core.models.pet.PetDetail
import com.xando.core.models.pet.PetDraft
import com.xando.core.models.pet.PetDraftPhoto
import com.xando.core.models.pet.PetSummary
import com.xando.data.pet.model.PetCardResponse
import com.xando.data.pet.model.toRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Репозиторий данных о питомцах.
 *
 * @param remoteDataSource Запросы к серверу, связанные с питомцами.
 * @param petDao Питомцы в локальной базе.
 * @param context Контекст приложения для чтения выбранного фото по Uri.
 */
class PetRepository @Inject internal constructor(
    private val remoteDataSource: PetRemoteDataSource,
    private val petDao: PetDao,
    @param:ApplicationContext private val context: Context,
) {

    /**
     * Питомцы текущего пользователя из локальной базы в порядке создания. Актуализируются через
     * [refreshPets].
     */
    fun observePets(): Flow<List<PetSummary>> {
        return petDao.observeOwnPets()
            .map { pets -> pets.map { it.toSummary() } }
            .distinctUntilChanged()
    }

    /**
     * Загружает питомцев текущего пользователя с сервера и приводит к ним локальную базу.
     *
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun refreshPets() {
        val pets = remoteDataSource.getPets()
        petDao.replaceOwnPets(
            pets = pets.map { it.toEntity() },
            interests = pets.flatMap { it.toInterestEntities() },
        )
    }

    /**
     * Питомец из локальной базы; `null`, если его там нет. Актуализируется через [refreshPet].
     *
     * @param petId Идентификатор питомца.
     */
    fun observePet(petId: String): Flow<PetDetail?> {
        return petDao.observePet(petId)
            .map { it?.toDetail() }
            .distinctUntilChanged()
    }

    /**
     * Загружает карточку питомца с сервера и сохраняет её в локальную базу. Если питомца на сервере
     * нет, удаляет его и из локальной базы.
     *
     * @param petId Идентификатор питомца.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался, в том числе
     * [NotFoundException], если питомца нет.
     */
    suspend fun refreshPet(petId: String) {
        val card = try {
            remoteDataSource.getPet(petId)
        } catch (e: NotFoundException) {
            petDao.deletePet(petId)
            throw e
        }
        savePetCard(card)
    }

    /**
     * Сохраняет питомца на сервере: создаёт нового или обновляет существующего с [PetDraft.petId]. Ответ
     * сервера записывается в локальную базу.
     *
     * Если [PetDraft.photo] требует новое фото, сначала загружает его; удаление текущего фото сервер
     * выполняет сам.
     *
     * @param draft Черновик формы: что сохранить и что сделать с фото.
     * @throws IllegalStateException Если новое фото не удалось прочитать по его Uri.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun savePet(draft: PetDraft) {
        val photoBytes = (draft.photo as? PetDraftPhoto.Replace)?.let { readPhoto(it.localUri) }
        val photoFileId = photoBytes?.let { remoteDataSource.uploadPhoto(it) }
        val request = draft.toRequest(photoFileId)

        val petId = draft.petId
        val card = if (petId == null) {
            remoteDataSource.createPet(request)
        } else {
            remoteDataSource.updatePet(petId, request)
        }

        savePetCard(card)
    }

    /**
     * Обновляет питомца с сервера и отдаёт его.
     *
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     * @throws IllegalStateException Если питомца не удалось прочитать из локальной базы.
     */
    @Deprecated("Используйте observePet() и refreshPet()")
    suspend fun getPetDetail(petId: String): PetDetail {
        refreshPet(petId)
        return observePet(petId).first() ?: error("Питомец $petId не прочитан из базы")
    }

    private suspend fun savePetCard(card: PetCardResponse) {
        petDao.savePet(
            pet = card.pet.toEntity(),
            interests = card.pet.toInterestEntities(),
            users = card.owners.map { it.user.toEntity() },
            owners = card.toOwnerEntities(),
        )
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
