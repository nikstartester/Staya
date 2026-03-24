@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Модель ответа при успешной авторизации. */
@Serializable
internal data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)