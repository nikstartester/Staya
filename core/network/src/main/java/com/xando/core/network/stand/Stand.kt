package com.xando.core.network.stand

/**
 * Серверные окружения (стенды) приложения.
 *
 * @property baseUrl Базовый URL стенда, к которому будут направляться запросы.
 */
enum class Stand(val baseUrl: String) {
    PROD("http://10.0.2.2:8080/api/v1"),
    TEST("http://10.0.2.2:8080/api/v1")
}
