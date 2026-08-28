package com.xando.data.user.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Hilt-модуль, предоставляющий зависимости данных пользователя. */
@Module
@InstallIn(SingletonComponent::class)
object UserDataModule {

    /** @SelfDocumented */
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)
}
