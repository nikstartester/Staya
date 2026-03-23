package com.xando.data.auth

/** Коды ошибок конфликта (409) для авторизации. */
object AuthConflictCodes {
    /** @SelfDocumented */
    const val EMAIL_EXISTS = "EMAIL_EXISTS"
    /** @SelfDocumented */
    const val LOGIN_EXISTS = "LOGIN_EXISTS"
}

/** Коды клиентских ошибок (4xx) для авторизации. */
object AuthClientCodes {
    /** @SelfDocumented */
    const val INVALID_CODE = "INVALID_CODE"
}