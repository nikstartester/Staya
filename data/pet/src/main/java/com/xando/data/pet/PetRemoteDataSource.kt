package com.xando.data.pet

import com.xando.core.network.NetworkChecker
import com.xando.core.network.multipartFile
import com.xando.core.network.withApiException
import com.xando.data.pet.model.FileUploadResponse
import com.xando.data.pet.model.PetCardResponse
import com.xando.data.pet.model.PetProfileResponse
import com.xando.data.pet.model.PetsListResponse
import com.xando.data.pet.model.SavePetRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

/**
 * Запросы к серверу, связанные с питомцами.
 *
 * @param httpClient HTTP-клиент для сетевых запросов.
 * @param networkChecker Проверка наличия подключения к интернету.
 */
internal class PetRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val networkChecker: NetworkChecker,
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
     * Загружает питомцев текущего пользователя.
     *
     * @return Профили питомцев в порядке создания.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun getPets(): List<PetProfileResponse> {
        return withApiException(networkChecker) {
            httpClient.get(PETS_URL).body<PetsListResponse>().pets
        }
    }

    /**
     * Загружает карточку питомца.
     *
     * @param petId Идентификатор питомца.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun getPet(petId: String): PetCardResponse {
        return withApiException(networkChecker) {
            httpClient.get("$PETS_URL/$petId").body<PetCardResponse>()
        }
    }

    /**
     * Создаёт питомца.
     *
     * @return Карточка созданного питомца.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun createPet(request: SavePetRequest): PetCardResponse {
        return withApiException(networkChecker) {
            httpClient.post(PETS_URL) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<PetCardResponse>()
        }
    }

    /**
     * Обновляет питомца [petId].
     *
     * @return Карточка обновлённого питомца.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun updatePet(petId: String, request: SavePetRequest): PetCardResponse {
        return withApiException(networkChecker) {
            httpClient.put("$PETS_URL/$petId") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<PetCardResponse>()
        }
    }

    /**
     * Загружает фото питомца на сервер.
     *
     * @param bytes Содержимое фото в формате JPEG.
     * @return Идентификатор загруженного файла.
     * @throws com.xando.core.api_models.ApiException Если запрос не удался.
     */
    suspend fun uploadPhoto(bytes: ByteArray): String {
        return withApiException(networkChecker) {
            httpClient.post(FILE_UPLOAD_URL) {
                setBody(
                    multipartFile(
                        bytes = bytes,
                        contentType = PET_PHOTO_CONTENT_TYPE,
                        fileName = PET_PHOTO_FILE_NAME,
                        fields = mapOf(CATEGORY_FIELD to PET_PHOTO_CATEGORY),
                    )
                )
            }.body<FileUploadResponse>().fileId
        }
    }
}
