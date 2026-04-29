package com.xando.pets_list.di

import com.xando.navigation_api.EntryBuilder
import com.xando.pets_list.navigation.petsListEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**@SelfDocumented */
@Module
@InstallIn(ActivityRetainedComponent::class)
object PetListNavigationModule {

    /**@SelfDocumented */
    @Provides
    @IntoSet
    fun providePetListEntryBuilder(): EntryBuilder {
        return EntryBuilder { navigationController ->
            petsListEntryBuilder(
                navigationController = navigationController,
            )
        }
    }
}
