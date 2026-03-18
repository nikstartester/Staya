package com.xando.core.network.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.crypto.tink.Aead
import com.google.crypto.tink.subtle.Base64
import com.xando.core.network.crypto.createAead
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import java.security.GeneralSecurityException

/**
 * Хранилище токенов авторизации с шифрованием через [Aead].
 * Считать его единственным источником истины об авторизации!
 *
 * Токены хранятся в [DataStore] в зашифрованном виде (AES256-GCM).
 * При повреждении ключей шифрования данные автоматически очищаются.
 *
 * @param prefs DataStore для персистентного хранения.
 * @param context Контекст приложения для доступа к Android Keystore.
 */
class TokenStorage(private val prefs: DataStore<Preferences>, private val context: Context) {

    companion object {
        private const val ACCESS_TOKEN_KEY_NAME = "access_token"
        private const val REFRESH_TOKEN_KEY_NAME = "refresh_token"
    }

    private val aead: Aead by lazy { createAead(context) }

    private val accessTokenKey = stringPreferencesKey(ACCESS_TOKEN_KEY_NAME)
    private val refreshTokenKey = stringPreferencesKey(REFRESH_TOKEN_KEY_NAME)

    /**
     * Поток текущих токенов.
     *
     * Эмитит [TokenPair] при каждом изменении данных в DataStore.
     * При ошибке дешифрования [GeneralSecurityException] автоматически
     * очищает хранилище и возвращает пустую пару.
     */
    val tokens: Flow<TokenPair> = prefs.data.transform { data ->
        try {
            emit(
                TokenPair(
                    access = data[accessTokenKey]?.let { decrypt(it) },
                    refresh = data[refreshTokenKey]?.let { decrypt(it) }
                )
            )
        } catch (_: GeneralSecurityException) {
            clear()
        }
    }

    /**
     * Сохраняет пару токенов в зашифрованном виде.
     *
     * @param access Access-токен.
     * @param refresh Refresh-токен.
     */
    suspend fun save(access: String, refresh: String) {
        prefs.edit {
            it[accessTokenKey] = encrypt(access)
            it[refreshTokenKey] = encrypt(refresh)
        }
    }

    /** Полностью очищает хранилище токенов. */
    suspend fun clear() {
        prefs.edit { it.clear() }
    }

    private fun encrypt(value: String): String =
        Base64.encodeToString(
            aead.encrypt(value.toByteArray(), null),
            Base64.DEFAULT
        )

    private fun decrypt(encrypted: String): String =
        String(
            aead.decrypt(
                Base64.decode(encrypted, Base64.DEFAULT), null
            )
        )
}
