package com.xando.pet_form.di

import com.xando.navigation_api.EntryBuilder
import com.xando.pet_form.navigation.petFormEntryBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**@SelfDocumented */
@Module
@InstallIn(ActivityRetainedComponent::class)
object PetFormNavigationModule {

    /**@SelfDocumented */
    @Provides
    @IntoSet
    fun providePetFormEntryBuilder(): EntryBuilder {
        return EntryBuilder { navigationController ->
            petFormEntryBuilder(navigationController = navigationController)
        }
    }
}
