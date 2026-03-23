@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Модель запроса переотправки кода подтверждения. */
@Serializable
internal data class ResendCodeRequest(
    val email: String,
    val purpose: String = "registration",
)