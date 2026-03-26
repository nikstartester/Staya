package com.xando.core.network.client

import androidx.annotation.AnyThread
import com.xando.core.network.auth.TokenStorage
import com.xando.core.network.auth.installTokenAuth
import com.xando.core.network.stand.StandManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO

/**
 * Фабрика для создания настроенного [HttpClient].
 *
 * Устанавливает плагины: динамический базовый URL, Bearer-аутентификацию и JSON-сериализацию.
 */
internal class HttpClientFactory(
    private val tokenStorage: TokenStorage,
    private val standManager: StandManager,
    private val config: NetworkConfig
) {

    /**
     * Создаёт и возвращает настроенный [HttpClient] с движком CIO.
     */
    @AnyThread
    fun create(): HttpClient = HttpClient(CIO) {
        expectSuccess = true
        installDynamicBaseUrl(standManager)
        installTokenAuth(tokenStorage, config)
        installJson(config)
    }
}
