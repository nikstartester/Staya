package com.xando.data.user.di

import com.xando.data.image.PhotoLocalStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Всё, что относится к фотографии профиля пользователя.
 *
 * Квалификатор помечает ресурс, а не экран: каталог копий и предельный размер одинаковы для любого
 * экрана, который правит аватар, и объявлены здесь один раз. Экран добавляет от себя только метку
 * отправки, по которой узнаёт свой результат.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserAvatar

/**@SelfDocumented*/
@Module
@InstallIn(SingletonComponent::class)
object AvatarUploadModule {

    /** Каталог копий фотографии профиля в кеше приложения. */
    private const val DIRECTORY_NAME = "user_avatar"

    /**@SelfDocumented*/
    @Provides
    @Singleton
    @UserAvatar
    fun provideAvatarLocalStorage(factory: PhotoLocalStorage.Factory): PhotoLocalStorage =
        factory.create(directoryName = DIRECTORY_NAME)
}
