package com.xando.data.user

import android.content.Context
import android.net.Uri
import com.xando.core.network.NetworkChecker
import com.xando.core.network.withApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Репозиторий данных пользователя.
 *
 * @param httpClient HTTP-клиент для сетевых запросов.
 * @param networkChecker Проверка наличия подключения к интернету.
 * @param context Контекст приложения для чтения выбранного файла по [Uri].
 */
class UserRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val networkChecker: NetworkChecker,
    @param:ApplicationContext private val context: Context,
) {

    private companion object {
        const val AVATAR_URL = "/users/me/avatar"
        const val AVATAR_PART_NAME = "file"
        const val AVATAR_FILE_NAME = "avatar"

        /** [AvatarLocalStorage] отдаёт копию всегда в этом формате. */
        const val AVATAR_CONTENT_TYPE = "image/jpeg"
    }

    /**
     * Загружает фотографию профиля. Требует авторизованного пользователя.
     *
     * @param photoUri Uri выбранной пользователем фотографии.
     * @throws IllegalStateException Если файл по [photoUri] не удалось прочитать.
     */
    suspend fun uploadAvatar(photoUri: Uri) {
        val contentResolver = context.contentResolver

        val bytes = withContext(Dispatchers.IO) {
            contentResolver.openInputStream(photoUri)?.use { it.readBytes() }
        } ?: error("Не удалось прочитать файл по uri: $photoUri")

        withApiException(networkChecker) {
            httpClient.put(AVATAR_URL) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = AVATAR_PART_NAME,
                                value = bytes,
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, AVATAR_CONTENT_TYPE)
                                    append(HttpHeaders.ContentDisposition, "filename=\"$AVATAR_FILE_NAME\"")
                                }
                            )
                        }
                    )
                )
            }
        }
    }
}
