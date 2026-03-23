package com.xando.data.auth

import com.xando.core.network.auth.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Предоставляет состояние авторизации пользователя.
 *
 * @param tokenStorage Хранилище токенов авторизации.
 */
class AuthStateProvider @Inject constructor(tokenStorage: TokenStorage) {

    /** Поток состояния авторизации. `true` — авторизован, `false` — нет. */
    val isAuthorized: Flow<Boolean> = tokenStorage.tokens
        .map { it.access != null }
        .distinctUntilChanged()
}