package com.xando.core.database

import com.xando.core.common.session.SessionStartListener
import javax.inject.Inject

/**
 * Очистка локальной базы целиком в начале сессии.
 *
 * @param database Локальная база приложения.
 */
internal class DatabaseCleaner @Inject constructor(private val database: StayaDatabase) : SessionStartListener {

    override suspend fun onSessionStart() {
        database.clearAllTables()
    }
}
