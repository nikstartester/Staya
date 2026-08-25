package com.xando.data.auth

import com.xando.core.api_models.ConflictException
import com.xando.core.api_models.ForbiddenException
import com.xando.core.api_models.UnauthorizedException
import com.xando.core.api_models.ValidationException
import com.xando.core.models.auth.data.SignUpData
import com.xando.core.network.NetworkChecker
import com.xando.core.network.auth.TokenStorage
import com.xando.core.network.withApiException
import com.xando.data.auth.model.LoginRequest
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
 * Выполняет запросы авторизации, регистрации, подтверждения email и переотправки кода.
 *
 * @param httpClient HTTP-клиент для сетевых запросов.
 * @param tokenStorage Хранилище токенов авторизации.
 * @param networkChecker Проверка наличия подключения к интернету.
 */
class AuthRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val tokenStorage: TokenStorage,
    private val networkChecker: NetworkChecker,
) {

    /**
     * Авторизует пользователя по email/логину и паролю. При успехе сохраняет токены в [TokenStorage].
     *
     * @param emailOrLogin Email или логин пользователя.
     * @param password Пароль.
     * @throws UnauthorizedException При неверных учётных данных.
     * @throws ForbiddenException При заблокированном аккаунте.
     */
    suspend fun login(emailOrLogin: String, password: String) {
        withApiException(networkChecker) {
            val response = httpClient.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(emailOrLogin = emailOrLogin, password = password))
            }.body<LoginResponse>()

            tokenStorage.save(access = response.accessToken, refresh = response.refreshToken)
        }
    }

    /**
     * Регистрирует пользователя. При успехе необходимо подтвердить email через [verifyEmail].
     *
     * @param data Данные для регистрации.
     * @throws ValidationException При невалидных данных. Код ошибки: [AuthValidationCodes.INVALID_FIRST_NAME],
     *   [AuthValidationCodes.INVALID_LAST_NAME], [AuthValidationCodes.INVALID_EMAIL], [AuthValidationCodes.WEAK_PASSWORD].
     * @throws ConflictException При конфликте данных. Код ошибки: [AuthConflictCodes.EMAIL_ALREADY_EXISTS], [AuthConflictCodes.LOGIN_ALREADY_EXISTS].
     */
    suspend fun signUp(data: SignUpData) {
        withApiException(networkChecker) {
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
     * @throws ValidationException При невалидном коде. Коды ошибок: [AuthValidationCodes.INVALID_CODE],
     *   [AuthValidationCodes.CODE_NOT_FOUND], [AuthValidationCodes.CODE_EXPIRED], [AuthValidationCodes.CODE_MAX_ATTEMPTS].
     */
    suspend fun verifyEmail(email: String, code: String) {
        withApiException(networkChecker) {
            val response = httpClient.post("/auth/register/verify-email") {
                contentType(ContentType.Application.Json)
                setBody(VerifyEmailRequest(email = email, code = code))
            }.body<LoginResponse>()

            tokenStorage.save(access = response.accessToken, refresh = response.refreshToken)
        }
    }

    /** @SelfDocumented */
    suspend fun resendCode(email: String) {
        withApiException(networkChecker) {
            httpClient.post("/auth/register/resend-code") {
                contentType(ContentType.Application.Json)
                setBody(ResendCodeRequest(email = email))
            }
        }
    }
}