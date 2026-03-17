package com.xando.core.network.crypto

import android.content.Context
import androidx.annotation.WorkerThread
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

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
        .withSharedPref(context, "token_keyset", "token_keyset_prefs")
        .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
        .withMasterKeyUri("android-keystore://token_master_key")
        .build()
        .keysetHandle
    return keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead::class.java)
}
