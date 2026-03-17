package com.xando.core.network.auth

import com.xando.core.network.client.NetworkConfig
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

/**
 * Устанавливает Ktor-плагин Bearer-аутентификации с автоматическим обновлением токенов.
 *
 * Поведение при обновлении:
 * - 401/403 от сервера — токены очищаются через [TokenStorage.clear].
 * - Сетевая ошибка или невалидный ответ — токены не затрагиваются, запрос завершается без авторизации.
 *
 * @param tokenStorage Хранилище токенов.
 * @param config Конфигурация сетевого модуля с URL для обновления токенов.
 */
internal fun HttpClientConfig<*>.installTokenAuth(tokenStorage: TokenStorage, config: NetworkConfig) {
    install(Auth) {
        bearer {
            loadTokens {
                val tokens = tokenStorage.tokens.first()
                val access = tokens.access ?: return@loadTokens null
                val refresh = tokens.refresh ?: return@loadTokens null
                BearerTokens(access, refresh)
            }
            refreshTokens {
                val refresh = tokenStorage.tokens.first().refresh ?: return@refreshTokens null

                try {
                    val response = client.post(config.refreshUrl) {
                        markAsRefreshTokenRequest()
                        setBody(AuthRefreshRequest(refresh))
                    }
                    val tokens = response.body<TokensDto>()
                    tokenStorage.save(tokens.accessToken, tokens.refreshToken)
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                }
                catch (ex: CancellationException) {
                    throw ex
                }
                catch (ex: ClientRequestException) {
                    if (ex.response.status in listOf(HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden)) {
                        tokenStorage.clear()
                    }
                    null
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}
