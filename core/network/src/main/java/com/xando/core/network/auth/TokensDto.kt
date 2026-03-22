package com.xando.core.network.auth

import kotlinx.serialization.Serializable

/** Ответ сервера на запрос обновления токенов. */
@Serializable
internal data class TokensDto(
    val accessToken: String,
    val refreshToken: String
)
