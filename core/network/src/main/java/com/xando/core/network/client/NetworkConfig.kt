package com.xando.core.network.client

/**
 * Конфигурация сетевого модуля.
 *
 * @property refreshUrl URL для обновления токенов.
 * @property jsonIgnoreUnknownKeys Игнорировать неизвестные поля при десериализации JSON.
 * @property jsonIsLenient Разрешить нестрогий парсинг JSON.
 */
internal data class NetworkConfig(
    val refreshUrl: String = DEFAULT_REFRESH_URL,
    val jsonIgnoreUnknownKeys: Boolean = true,
    val jsonIsLenient: Boolean = true,
) {
    companion object {
        private const val DEFAULT_REFRESH_URL = "auth/refresh"
    }
}
