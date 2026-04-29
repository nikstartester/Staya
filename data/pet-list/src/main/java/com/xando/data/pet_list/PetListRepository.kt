package com.xando.data.pet_list

import com.xando.core.models.pet.PetSummary
import com.xando.core.network.NetworkChecker
import com.xando.core.network.withApiException
import com.xando.data.pet_list.model.PetsListResponse
import com.xando.data.pet_list.model.mapToDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

/**
 * Репозиторий для получения списка питомцев.
 *
 * @param httpClient HTTP-клиент для сетевых запросов.
 * @param networkChecker Проверка наличия подключения к интернету.
 */
class PetListRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val networkChecker: NetworkChecker,
) {

    /**
     * Получает список питомцев.
     *
     * @return Список кратких данных питомцев.
     */
    suspend fun getPetList(): List<PetSummary> {
        return withApiException(networkChecker) {
            val response = httpClient.get("/pets").body<PetsListResponse>()
            response.pets.map { it.mapToDomain() }
        }
    }
}
