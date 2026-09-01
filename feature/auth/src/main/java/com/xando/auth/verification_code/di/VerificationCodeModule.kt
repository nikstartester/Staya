package com.xando.auth.verification_code.di

import com.xando.auth.verification_code.VerificationCodeError
import com.xando.auth.verification_code.VerificationCodeFeedback
import com.xando.auth.verification_code.VerificationCodeFeedbackImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.flow.Flow

/**@SelfDocumented*/
@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class VerificationCodeModule {

    /**@SelfDocumented*/
    @Binds
    abstract fun bindVerificationCodeFeedback(impl: VerificationCodeFeedbackImpl): VerificationCodeFeedback

    /**
     * Поток ошибок для ViewModel шторки.
     *
     * Раздаётся отдельно от [VerificationCodeFeedback], чтобы получателю доставался только поток,
     * без доступа к отправке событий.
     */
    companion object {

        /**@SelfDocumented*/
        @Provides
        fun provideVerificationErrors(feedback: VerificationCodeFeedback): Flow<VerificationCodeError> =
            feedback.errors
    }
}
