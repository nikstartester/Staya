@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Модель запроса смены пароля по коду подтверждения. */
@Serializable
internal data class PasswordResetConfirmRequest(
    val email: String,
    val code: String,
    val newPassword: String,
)
