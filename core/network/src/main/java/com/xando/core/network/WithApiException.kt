package com.xando.core.network

import com.xando.core.api_models.ClientException
import com.xando.core.api_models.ConflictException
import com.xando.core.api_models.ForbiddenException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.NotFoundException
import com.xando.core.api_models.ServerException
import com.xando.core.api_models.TimeoutException
import com.xando.core.api_models.UnauthorizedException
import com.xando.core.api_models.UnknownApiException
import com.xando.core.network.model.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Выполняет [block] и маппит исключения Ktor в [ApiException].
 *
 * Парсит тело ответа сервера как [ErrorResponse] для получения кода и сообщения ошибки.
 *
 * @param block Suspend-блок с сетевым вызовом.
 * @return Результат выполнения [block].
 * @throws ApiException При любой ошибке сети или сервера.
 */
suspend fun <T> withApiException(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: ResponseException) {
        val body = e.response.body<ErrorResponse>()
        val message = body.error.message
        throw when (e.response.status) {
            HttpStatusCode.Unauthorized -> UnauthorizedException(message)
            HttpStatusCode.Forbidden -> ForbiddenException(message)
            HttpStatusCode.NotFound -> NotFoundException(message)
            HttpStatusCode.Conflict -> ConflictException(code = body.error.code, message = message)
            else -> when (e.response.status.value) {
                in 400..499 -> ClientException(code = body.error.code, message = message)
                in 500..599 -> ServerException(message)
                else -> UnknownApiException(e)
            }
        }
    } catch (e: SocketTimeoutException) {
        throw TimeoutException(e)
    } catch (e: IOException) {
        throw NetworkException(e)
    } catch (e: Exception) {
        throw UnknownApiException(e)
    }
}