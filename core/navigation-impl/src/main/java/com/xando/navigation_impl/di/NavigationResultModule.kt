package com.xando.navigation_impl.di

import com.xando.navigation_api.NavigationResultStore
import com.xando.navigation_impl.NavigationResultStoreImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**@SelfDocumented*/
@Module
@InstallIn(SingletonComponent::class)
interface NavigationResultModule {

    /**@SelfDocumented*/
    @Binds
    fun bindNavigationResultStore(impl: NavigationResultStoreImpl): NavigationResultStore
}
