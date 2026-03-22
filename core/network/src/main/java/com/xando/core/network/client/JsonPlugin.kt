package com.xando.core.network.client

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Устанавливает плагин JSON-сериализации для Ktor-клиента.
 *
 * @param config Конфигурация сетевого модуля с настройками JSON.
 */
internal fun HttpClientConfig<*>.installJson(config: NetworkConfig) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = config.jsonIgnoreUnknownKeys
            isLenient = config.jsonIsLenient
        })
    }
}
