package com.xando.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.auth.navigation.internal.ForgotPasswordKey
import com.xando.auth.navigation.internal.SignUpKey
import com.xando.auth.ui.forgot_password.ForgotPasswordScreen
import com.xando.auth.ui.login.LoginScreen
import com.xando.auth.ui.login.LoginViewModel
import com.xando.auth.ui.sign_up.SignUpScreen
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.auth.LoginKey
import com.xando.navigation_api.features.home.HomeKey

/**
 * Регистрирует navigation entries для модуля auth
 */
internal fun EntryProviderScope<NavKey>.authEntryBuilder(navigationController: NavigationController) {
    // TODO: Простенькие примеры
    entry<LoginKey> {
        LoginScreen(
            viewModel = LoginViewModel(), //TODO: потом заменить на hiltViewModel
            onLoginSuccess = {
                navigationController.navigateAndClearStack(HomeKey)
            },
            onSignUpClick = { prefilledEmail ->
                navigationController.navigateTo(
                    SignUpKey
                )
            },
            onForgotPasswordClick = {
                navigationController.navigateTo(ForgotPasswordKey)
            }
        )
    }

    entry<SignUpKey> { key ->
        SignUpScreen(
            onSignUpSuccess = {
                navigationController.navigateAndClearStack(HomeKey)
            },
            onBackClick = {
                navigationController.navigateBack()
            },
            onLoginClick = {
                navigationController.navigateAndPopUpTo(
                    destination = LoginKey,
                    popUpTo = LoginKey,
                    inclusive = false
                )
            }
        )
    }

    entry<ForgotPasswordKey> {
        ForgotPasswordScreen(
            onBackClick = {
                navigationController.navigateBack()
            },
            onPasswordResetSuccess = {
                navigationController.navigateAndPopUpTo(
                    destination = LoginKey,
                    popUpTo = LoginKey,
                    inclusive = false
                )
            }
        )
    }
}
