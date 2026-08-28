package com.xando.auth.sign_up.di

import com.xando.auth.sign_up.domain.use_case.AvatarUploadUseCase
import com.xando.data.user.AvatarUploader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

/**
 * Работа с фотографией профиля, начатой при регистрации.
 *
 * Квалификатор обязателен: без него биндинг [AvatarUploadUseCase] был бы общим на приложение, и
 * другой модуль, которому нужна своя метка, не смог бы объявить свой.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class SignUpAvatar

/**@SelfDocumented*/
@Module
@InstallIn(SingletonComponent::class)
internal object SignUpAvatarModule {

    /** Метка загрузок, начатых при регистрации: о них отчитывается auth-флоу и только он. */
    private const val ORIGIN_TAG = "sign_up_avatar_upload"

    /**@SelfDocumented*/
    @Provides
    @SignUpAvatar
    fun provideAvatarUploadUseCase(factory: AvatarUploader.Factory): AvatarUploadUseCase =
        AvatarUploadUseCase(factory.create(ORIGIN_TAG))
}
