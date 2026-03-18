package com.xando.core.network.auth

/**
 * Пара токенов авторизации.
 *
 * @property access Access-токен. `null`, если пользователь не авторизован.
 * @property refresh Refresh-токен. `null`, если пользователь не авторизован.
 */
data class TokenPair(val access: String?, val refresh: String?)
