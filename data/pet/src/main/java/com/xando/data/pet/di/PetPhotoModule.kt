package com.xando.data.pet.di

import com.xando.data.image.PhotoLocalStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Каталог локальных копий фотографии питомца.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PetPhoto

/**@SelfDocumented*/
@Module
@InstallIn(SingletonComponent::class)
object PetPhotoModule {

    /** Каталог копий фотографии питомца в кеше приложения. */
    private const val DIRECTORY_NAME = "pet_photo"

    // TODO: убрать зависимость data:pet от data:image.
    //  PhotoLocalStorage нужен только ViewModel формы, поэтому этот provide вместе с квалификатором
    //  должен переехать в feature:pet-form.
    /**@SelfDocumented*/
    @Provides
    @Singleton
    @PetPhoto
    fun providePetPhotoLocalStorage(factory: PhotoLocalStorage.Factory): PhotoLocalStorage =
        factory.create(directoryName = DIRECTORY_NAME)
}
