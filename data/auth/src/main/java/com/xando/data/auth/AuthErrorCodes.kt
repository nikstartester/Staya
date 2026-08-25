package com.xando.data.auth

/** Коды ошибок валидации (400) для авторизации. */
object AuthValidationCodes {
    /** @SelfDocumented */
    const val INVALID_FIRST_NAME = "INVALID_FIRST_NAME"

    /** @SelfDocumented */
    const val INVALID_LAST_NAME = "INVALID_LAST_NAME"

    /** @SelfDocumented */
    const val INVALID_EMAIL = "INVALID_EMAIL"

    /** @SelfDocumented */
    const val WEAK_PASSWORD = "WEAK_PASSWORD"

    /** @SelfDocumented */
    const val INVALID_CODE = "INVALID_CODE"

    /** Активного кода подтверждения не найдено — нужно запросить новый. */
    const val CODE_NOT_FOUND = "CODE_NOT_FOUND"

    /** Срок действия кода подтверждения истёк — нужно запросить новый. */
    const val CODE_EXPIRED = "CODE_EXPIRED"

    /** Превышено число попыток ввода кода — нужно запросить новый. */
    const val CODE_MAX_ATTEMPTS = "CODE_MAX_ATTEMPTS"
}

/** Коды ошибок конфликта (409) для авторизации. */
object AuthConflictCodes {
    /** @SelfDocumented */
    const val EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS"

    /** @SelfDocumented */
    const val LOGIN_ALREADY_EXISTS = "LOGIN_ALREADY_EXISTS"
}