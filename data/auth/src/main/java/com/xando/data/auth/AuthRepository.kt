package com.xando.data.auth

import com.xando.core.api_models.ClientException
import com.xando.core.api_models.ConflictException
import com.xando.core.models.auth.data.SignUpData
import com.xando.core.network.auth.TokenStorage
import com.xando.core.network.withApiException
import com.xando.data.auth.model.LoginResponse
import com.xando.data.auth.model.ResendCodeRequest
import com.xando.data.auth.model.VerifyEmailRequest
import com.xando.data.auth.model.toRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

/**
 * Репозиторий авторизации.
 *
 * Выполняет запросы регистрации, подтверждения email и переотправки кода.
 *
 * @param httpClient HTTP-клиент для сетевых запросов.
 * @param tokenStorage Хранилище токенов авторизации.
 */
class AuthRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
) {

    /**
     * Регистрирует пользователя. При успехе необходимо подтвердить email через [verifyEmail].
     *
     * @param data Данные для регистрации.
     * @throws ConflictException При конфликте данных. Код ошибки: [AuthConflictCodes.EMAIL_EXISTS], [AuthConflictCodes.LOGIN_EXISTS].
     */
    suspend fun signUp(data: SignUpData) {
        withApiException {
            httpClient.post("/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(data.toRequest())
            }
        }
    }

    /**
     * Подтверждает email кодом. При успехе сохраняет токены в [TokenStorage].
     *
     * @param email Email пользователя.
     * @param code Код подтверждения.
     * @throws ClientException При невалидном коде. Код ошибки: [AuthClientCodes.INVALID_CODE].
     */
    suspend fun verifyEmail(email: String, code: String) {
        withApiException {
            val response = httpClient.post("/auth/verify-email") {
                contentType(ContentType.Application.Json)
                setBody(VerifyEmailRequest(email = email, code = code))
            }.body<LoginResponse>()

            tokenStorage.save(access = response.accessToken, refresh = response.refreshToken)
        }
    }

    /** @SelfDocumented */
    suspend fun resendCode(email: String) {
        withApiException {
            httpClient.post("/auth/resend-code") {
                contentType(ContentType.Application.Json)
                setBody(ResendCodeRequest(email = email))
            }
        }
    }
}