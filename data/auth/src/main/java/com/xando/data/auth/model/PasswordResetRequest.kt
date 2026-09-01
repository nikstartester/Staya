@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Модель запроса кода для сброса пароля. */
@Serializable
internal data class PasswordResetRequest(
    val email: String,
)
