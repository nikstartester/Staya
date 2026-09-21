package com.xando.data.pet

import android.content.Context
import androidx.core.net.toUri
import com.xando.core.models.pet.PetDetail
import com.xando.core.models.pet.PetDraft
import com.xando.core.models.pet.PetDraftPhoto
import com.xando.core.models.pet.PetSummary
import com.xando.core.network.NetworkChecker
import com.xando.core.network.multipartFile
import com.xando.core.network.withApiException
import com.xando.data.pet.model.FileUploadResponse
import com.xando.data.pet.model.PetCardResponse
import com.xando.data.pet.model.PetsListResponse
import com.xando.data.pet.model.mapToDomain
import com.xando.data.pet.model.toRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Репозиторий данных о питомцах.
 *
 * @param httpClient HTTP-клиент для сетевых запросов.
 * @param networkChecker Проверка наличия подключения к интернету.
 * @param context Контекст приложения для чтения выбранного фото по Uri.
 */
class PetRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val networkChecker: NetworkChecker,
    @param:ApplicationContext private val context: Context,
) {

    private companion object {
        const val PETS_URL = "/pets"
        const val FILE_UPLOAD_URL = "/files/upload"

        /** Поле формы с категорией файла на сервере. */
        const val CATEGORY_FIELD = "category"
        const val PET_PHOTO_CATEGORY = "pet-photos"
        const val PET_PHOTO_FILE_NAME = "pet_photo"

        /** [PhotoLocalStorage][com.xando.data.image.PhotoLocalStorage] отдаёт копию всегда в этом формате. */
        const val PET_PHOTO_CONTENT_TYPE = "image/jpeg"
    }

    /**
     * Получает список питомцев.
     *
     * @return Список кратких данных питомцев.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun getPetList(): List<PetSummary> {
        return withApiException(networkChecker) {
            val response = httpClient.get(PETS_URL).body<PetsListResponse>()
            response.pets.map { it.mapToDomain() }
        }
    }

    /**
     * Получает полную информацию о питомце.
     *
     * @param petId Идентификатор питомца.
     * @return Полная информация о питомце.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun getPetDetail(petId: String): PetDetail {
        return withApiException(networkChecker) {
            httpClient.get("$PETS_URL/$petId").body<PetCardResponse>().mapToDomain()
        }
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

        return withApiException(networkChecker) {
            val photoFileId = photoBytes?.let { uploadPhoto(it) }
            val body = draft.toRequest(photoFileId)

            val response = if (draft.petId == null) {
                httpClient.post(PETS_URL) {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            } else {
                httpClient.put("$PETS_URL/${draft.petId}") {
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            }

            response.body<PetCardResponse>().mapToDomain()
        }
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

    /**
     * Загружает фото [bytes] на сервер.
     *
     * @return Идентификатор загруженного файла.
     */
    private suspend fun uploadPhoto(bytes: ByteArray): String {
        val response = httpClient.post(FILE_UPLOAD_URL) {
            setBody(
                multipartFile(
                    bytes = bytes,
                    contentType = PET_PHOTO_CONTENT_TYPE,
                    fileName = PET_PHOTO_FILE_NAME,
                    fields = mapOf(CATEGORY_FIELD to PET_PHOTO_CATEGORY),
                )
            )
        }.body<FileUploadResponse>()

        return response.fileId
    }
}
