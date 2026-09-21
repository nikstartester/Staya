package com.xando.navigation_impl

import com.xando.navigation_api.NavigationResultStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [NavigationResultStore].
 *
 * Результат лежит в общей мапе, пока его не заберут: подписка на [results] может появиться позже
 * [setResult] - например, вызывающий экран пересоздали вместе с процессом, пока пользователь был в
 * вызываемом. Забранный результат сразу удаляется, поэтому повторной доставки при пересоздании
 * подписки не будет, а мапа не растёт.
 */
@Singleton
class NavigationResultStoreImpl @Inject constructor() : NavigationResultStore {

    private val results = MutableStateFlow<Map<String, Any>>(emptyMap())

    override fun <T : Any> setResult(requestKey: String, result: T) {
        results.update { it + (requestKey to result) }
    }

    /**
     * Приведение типа не проверяется - гарантию даёт сам вызывающий код: `requestKey` он же и
     * генерирует, и с самого начала знает, значение какого типа под ним ожидает.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> results(requestKey: String): Flow<T> = results
        .mapNotNull { it[requestKey] }
        .onEach { results.update { current -> current - requestKey } }
        .map { it as T }
}
