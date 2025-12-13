package com.xando.auth.di

import com.xando.auth.navigation.authEntryBuilder
import com.xando.navigation_api.EntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**@SelfDocumented*/
@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthNavigationModule {

    /**@SelfDocumented*/
    @Provides
    @IntoSet
    fun provideAuthEntryBuilder(): EntryBuilder {
        return EntryBuilder { navigationController ->
            authEntryBuilder(navigationController)
        }
    }
}
