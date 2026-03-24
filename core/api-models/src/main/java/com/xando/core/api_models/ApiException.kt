package com.xando.core.api_models

/**
 * Базовое исключение для всех ошибок API.
 *
 * @param message Сообщение об ошибке.
 * @param cause Причина ошибки.
 */
sealed class ApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Ошибка сети — отсутствует подключение к интернету. */
class NetworkException(cause: Throwable) : ApiException("No connection", cause)

/** Превышено время ожидания запроса. */
class TimeoutException(cause: Throwable) : ApiException("Request timeout", cause)

/** Ошибка авторизации (401). */
class UnauthorizedException(message: String = "Unauthorized") : ApiException(message)

/** Доступ запрещён (403). */
class ForbiddenException(message: String = "Forbidden") : ApiException(message)

/** Ресурс не найден (404). */
class NotFoundException(message: String = "Not found") : ApiException(message)

/**
 * Конфликт данных (409).
 *
 * @property code Код ошибки с сервера для идентификации типа конфликта.
 */
class ConflictException(val code: String = "", message: String = "Conflict") : ApiException(message)

/**
 * Клиентская ошибка (4xx).
 *
 * @property code Код ошибки с сервера для идентификации типа ошибки.
 */
class ClientException(val code: String = "", message: String = "Client error") : ApiException(message)

/** Ошибка сервера (5xx). */
class ServerException(message: String = "Server error") : ApiException(message)

/** Неизвестная ошибка. */
class UnknownApiException(cause: Throwable) : ApiException("Unknown error", cause)