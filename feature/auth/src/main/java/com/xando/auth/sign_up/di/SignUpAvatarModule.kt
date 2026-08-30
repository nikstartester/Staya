package com.xando.auth.sign_up.di

import com.xando.auth.sign_up.domain.use_case.AvatarUploadUseCase
import com.xando.data.image.PhotoLocalStorage
import com.xando.data.image.PhotoUploader
import com.xando.data.user.AvatarUploadTarget
import com.xando.data.user.di.UserAvatar
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

/**
 * Работа с фотографией профиля, начатой при регистрации.
 *
 * Квалификатор обязателен: без него биндинг [AvatarUploadUseCase] был бы общим на приложение, и
 * другой экран, правящий аватар, не смог бы объявить свой.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class SignUpAvatar

/**@SelfDocumented*/
@Module
@InstallIn(SingletonComponent::class)
internal object SignUpAvatarModule {

    /** Метка отправок, начатых при регистрации: о них отчитывается auth-флоу и только он. */
    private const val ORIGIN_TAG = "sign_up_avatar_upload"

    /**
     * Хранилище приходит готовым: каталог и предельный размер задаёт ресурс. Регистрация добавляет
     * от себя только метку, по которой узнаёт свою отправку.
     */
    @Provides
    @SignUpAvatar
    fun provideAvatarUploadUseCase(
        @UserAvatar photoLocalStorage: PhotoLocalStorage,
        uploaderFactory: PhotoUploader.Factory,
    ): AvatarUploadUseCase = AvatarUploadUseCase(
        photoLocalStorage = photoLocalStorage,
        photoUploader = uploaderFactory.create(target = AvatarUploadTarget, originTag = ORIGIN_TAG),
    )
}
