package com.xando.core.network.crypto

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

private const val KEYSET_NAME = "token_keyset"
private const val KEYSET_PREFS_FILE = "token_keyset_prefs"
private const val KEY_TEMPLATE_AES256 = "AES256_GCM"
private const val MASTER_KEY_URI = "android-keystore://token_master_key"

/**
 * Создаёт экземпляр [Aead] для шифрования/дешифрования данных.
 *
 * Использует Android Keystore как мастер-ключ и SharedPreferences для хранения keyset.
 * При первом вызове генерирует ключ AES256-GCM.
 *
 * @param context Контекст приложения для доступа к SharedPreferences и Keystore.
 * @return Готовый к использованию [Aead]-примитив.
 */
@WorkerThread
internal fun createAead(context: Context): Aead {
    AeadConfig.register()
    val keysetHandle = AndroidKeysetManager.Builder()
        .withSharedPref(context, KEYSET_NAME, KEYSET_PREFS_FILE)
        .withKeyTemplate(KeyTemplates.get(KEY_TEMPLATE_AES256))
        .withMasterKeyUri(MASTER_KEY_URI)
        .build()
        .keysetHandle
    return keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
}
