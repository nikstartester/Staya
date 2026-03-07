package com.xando.auth.di

import com.xando.auth.ui.sign_up.SignUpFlowCoordinator
import com.xando.auth.ui.sign_up.SignUpFlowCoordinatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class SignUpFlowModule {

    @Binds
    abstract fun bindSignUpFlowCoordinator(impl: SignUpFlowCoordinatorImpl): SignUpFlowCoordinator
}
