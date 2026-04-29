package com.xando.core.network.stand

/**
 * Серверные окружения (стенды) приложения.
 *
 * @property baseUrl Базовый URL стенда, к которому будут направляться запросы.
 */
enum class Stand(val baseUrl: String) {
    // ip адрес компа с сервером в локальной сети
    PROD("http://192.168.1.120:8080/api/v1"),
    TEST("http://192.168.1.120:8080/api/v1")
}
