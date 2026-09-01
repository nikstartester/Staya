@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Модель ответа проверки логина на доступность.
 *
 * @property login Проверенный логин.
 * @property available `true`, если логин свободен.
 */
@Serializable
internal data class LoginAvailabilityResponse(
    val login: String,
    val available: Boolean,
)
