package com.xando.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** DataStore для хранения зашифрованных токенов авторизации. */
internal val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")

/** DataStore для хранения текущего серверного окружения. */
internal val Context.standDataStore: DataStore<Preferences> by preferencesDataStore(name = "stand_prefs")
