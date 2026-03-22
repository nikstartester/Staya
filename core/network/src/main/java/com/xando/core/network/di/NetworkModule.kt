package com.xando.core.network.di

import android.content.Context
import com.xando.core.network.auth.TokenStorage
import com.xando.core.network.client.HttpClientFactory
import com.xando.core.network.client.NetworkConfig
import com.xando.core.network.stand.StandManager
import com.xando.core.network.standDataStore
import com.xando.core.network.tokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/** Hilt-модуль, предоставляющий зависимости сетевого слоя. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** @SelfDocumented */
    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage =
        TokenStorage(context.tokenDataStore, context)

    /** @SelfDocumented */
    @Provides
    @Singleton
    fun provideStandManager(@ApplicationContext context: Context): StandManager =
        StandManager(context.standDataStore)

    /** @SelfDocumented */
    @Provides
    @Singleton
    fun provideHttpClient(
        tokenStorage: TokenStorage,
        standManager: StandManager
    ): HttpClient = HttpClientFactory(tokenStorage, standManager, NetworkConfig()).create()
}
