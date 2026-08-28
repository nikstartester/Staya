package com.xando.staya.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Приложение.
 *
 * Реализует [Configuration.Provider], чтобы WorkManager собирал воркеры через Hilt. По этой же
 * причине его автоматическая инициализация через `androidx.startup` отключена в манифесте.
 */
@HiltAndroidApp
class StayaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
