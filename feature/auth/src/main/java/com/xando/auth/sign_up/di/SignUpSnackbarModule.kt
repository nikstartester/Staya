package com.xando.auth.sign_up.di

import com.xando.auth.sign_up.snackbar.AvatarUploadSnackbarSource
import com.xando.design.ui.snackbar.BackgroundSnackbarSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**@SelfDocumented*/
@Module
@InstallIn(SingletonComponent::class)
internal interface SignUpSnackbarModule {

    /**@SelfDocumented*/
    @Binds
    @IntoSet
    fun bindAvatarUploadSnackbarSource(source: AvatarUploadSnackbarSource): BackgroundSnackbarSource
}