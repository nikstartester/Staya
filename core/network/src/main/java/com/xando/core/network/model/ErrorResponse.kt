@file:OptIn(InternalSerializationApi::class)

package com.xando.core.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Обёртка ответа сервера при ошибке. */
@Serializable
internal data class ErrorResponse(val error: ErrorBody)

/**
 * Тело ошибки из ответа сервера.
 *
 * @property code Код ошибки.
 * @property message Сообщение об ошибке.
 */
@Serializable
internal data class ErrorBody(val code: String, val message: String)