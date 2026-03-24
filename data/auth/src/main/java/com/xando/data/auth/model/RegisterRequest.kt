@file:OptIn(InternalSerializationApi::class)

package com.xando.data.auth.model

import com.xando.core.models.auth.data.SignUpData
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/** Модель запроса регистрации. */
@Serializable
internal data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val description: String? = null,
    val login: String? = null,
)

/** Маппинг [SignUpData] в [RegisterRequest]. */
internal fun SignUpData.toRequest() = RegisterRequest(
    email = email,
    password = password,
    firstName = firstName,
    lastName = lastName,
    description = description.takeIf { it.isNotBlank() },
    login = login.takeIf { it.isNotBlank() },
)