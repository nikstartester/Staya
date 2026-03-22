package com.xando.core.network.client

import com.xando.core.network.stand.StandManager
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.url
import kotlinx.coroutines.flow.first

/**
 * Устанавливает Ktor-плагин, подставляющий базовый URL текущего стенда к относительным путям запросов.
 *
 * @param standManager Менеджер серверных окружений.
 */
internal fun HttpClientConfig<*>.installDynamicBaseUrl(standManager: StandManager) {
    install(createClientPlugin("DynamicBaseUrl") {
        onRequest { request, _ ->
            val baseUrl = standManager.current.first().baseUrl
            val original = request.url.buildString()
            if (!original.startsWith("http")) {
                request.url(baseUrl + original)
            }
        }
    })
}
