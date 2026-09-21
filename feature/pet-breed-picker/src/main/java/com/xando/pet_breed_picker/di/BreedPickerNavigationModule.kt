package com.xando.pet_breed_picker.di

import com.xando.navigation_api.EntryBuilder
import com.xando.pet_breed_picker.navigation.breedPickerEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**@SelfDocumented */
@Module
@InstallIn(ActivityRetainedComponent::class)
object BreedPickerNavigationModule {

    /**@SelfDocumented */
    @Provides
    @IntoSet
    fun provideBreedPickerEntryBuilder(): EntryBuilder {
        return EntryBuilder { navigationController ->
            breedPickerEntryBuilder(navigationController = navigationController)
        }
    }
}
