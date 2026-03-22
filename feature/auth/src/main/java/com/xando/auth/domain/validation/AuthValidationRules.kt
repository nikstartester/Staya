package com.xando.auth.domain.validation

/**
 * Общие правила валидации для auth-флоу.
 */
internal object AuthValidationRules {
    /**
     * Минимально допустимая длина пароля для auth-флоу.
     */
    const val MIN_PASSWORD_LENGTH = 6
}