package com.xando.core.network.auth

import kotlinx.serialization.Serializable

/** @SelfDocumented */
@Serializable
internal data class AuthRefreshRequest(
    val refreshToken: String
)
