@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Модель запроса подтверждения email. */
@Serializable
internal data class VerifyEmailRequest(
    val email: String,
    val code: String,
)