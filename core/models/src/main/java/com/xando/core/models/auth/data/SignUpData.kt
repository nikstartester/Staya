package com.xando.core.models.auth.data

/** Данные для регистрации пользователя. */
data class SignUpData(
    val firstName: String,
    val lastName: String,
    val description: String,
    val login: String,
    val email: String,
    val password: String,
)