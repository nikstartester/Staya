package com.xando.auth.sign_up.di

import com.xando.auth.sign_up.EmailPasswordErrors
import com.xando.auth.sign_up.LoginError
import com.xando.auth.sign_up.SignUpFlowCoordinator
import com.xando.auth.sign_up.SignUpFlowCoordinatorImpl
import com.xando.auth.sign_up.SignUpFlowFeedback
import com.xando.auth.sign_up.SignUpFlowFeedbackImpl
import com.xando.auth.verification_code.VerificationCodeError
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.Flow

/**@SelfDocumented*/
@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class SignUpFlowModule {

    /**@SelfDocumented*/
    @Binds
    abstract fun bindSignUpFlowCoordinator(impl: SignUpFlowCoordinatorImpl): SignUpFlowCoordinator

    /**@SelfDocumented*/
    @Binds
    abstract fun bindSignUpFlowFeedback(impl: SignUpFlowFeedbackImpl): SignUpFlowFeedback

    /**
     * Потоки ошибок для ViewModel шагов.
     *
     * Раздаются отдельно от [SignUpFlowFeedback], чтобы получателю доставался только его поток,
     * без доступа к отправке событий на соседние шаги.
     */
    companion object {

        /**@SelfDocumented*/
        @Provides
        fun provideLoginErrors(feedback: SignUpFlowFeedback): Flow<LoginError> = feedback.loginErrors

        /**@SelfDocumented*/
        @Provides
        fun provideEmailPasswordErrors(feedback: SignUpFlowFeedback): Flow<EmailPasswordErrors> =
            feedback.emailPasswordErrors

        /**@SelfDocumented*/
        @Provides
        fun provideVerificationErrors(feedback: SignUpFlowFeedback): Flow<VerificationCodeError> =
            feedback.verificationErrors
    }
}