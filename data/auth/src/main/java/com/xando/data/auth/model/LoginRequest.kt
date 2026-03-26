@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**@SelfDocumented*/
@Serializable
internal data class LoginRequest(val emailOrLogin: String, val password: String)