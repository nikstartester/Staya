package com.xando.pet_interests_picker.di

import com.xando.navigation_api.EntryBuilder
import com.xando.pet_interests_picker.navigation.interestsPickerEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**@SelfDocumented */
@Module
@InstallIn(ActivityRetainedComponent::class)
object InterestsPickerNavigationModule {

    /**@SelfDocumented */
    @Provides
    @IntoSet
    fun provideInterestsPickerEntryBuilder(): EntryBuilder {
        return EntryBuilder { navigationController ->
            interestsPickerEntryBuilder(navigationController = navigationController)
        }
    }
}
