package com.xando.core.database.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.xando.core.common.session.SessionStartListener
import com.xando.core.database.DatabaseCleaner
import com.xando.core.database.StayaDatabase
import com.xando.core.database.pet.PetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/** Hilt-модуль, предоставляющий базу данных, её DAO и очистку базы в начале сессии. */
@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    private const val DATABASE_NAME = "staya.db"

    /**
     * База данных приложения. При откате на версию приложения со старой схемой данные удаляются:
     * миграций вниз нет.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StayaDatabase =
        Room.databaseBuilder<StayaDatabase>(context, DATABASE_NAME)
            .setDriver(AndroidSQLiteDriver())
            .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
            .build()

    /** Очистка базы в начале сессии. */
    @Provides
    @IntoSet
    fun provideDatabaseCleaner(cleaner: DatabaseCleaner): SessionStartListener = cleaner

    /** @SelfDocumented */
    @Provides
    fun providePetDao(database: StayaDatabase): PetDao = database.petDao()
}
