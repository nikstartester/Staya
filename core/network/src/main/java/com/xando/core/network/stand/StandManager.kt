package com.xando.core.network.stand

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Управляет текущим стендом (серверным окружением).
 *
 * Выбранный стенд сохраняется в [DataStore] и восстанавливается при перезапуске.
 * Если сохранённое значение невалидно, используется [Stand.PROD].
 *
 * @param prefs DataStore для хранения выбранного стенда.
 */
class StandManager(private val prefs: DataStore<Preferences>) {

    companion object {
        private const val STAND_KEY_NAME = "current_stand"
    }

    private val standKey = stringPreferencesKey(STAND_KEY_NAME)

    /** Поток текущего стенда. */
    val current: Flow<Stand> = prefs.data.map { data ->
        data[standKey]?.let { name -> Stand.entries.find { it.name == name } } ?: Stand.PROD
    }

    /** Переключает текущий стенд на [stand]. */
    suspend fun switchTo(stand: Stand) {
        prefs.edit { it[standKey] = stand.name }
    }
}
