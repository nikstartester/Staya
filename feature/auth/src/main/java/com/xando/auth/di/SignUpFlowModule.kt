package com.xando.auth.di

import com.xando.auth.presentation.sign_up.SignUpFlowCoordinator
import com.xando.auth.presentation.sign_up.SignUpFlowCoordinatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

/**@SelfDocumented*/
@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class SignUpFlowModule {

    /**@SelfDocumented*/
    @Binds
    abstract fun bindSignUpFlowCoordinator(impl: SignUpFlowCoordinatorImpl): SignUpFlowCoordinator
}
