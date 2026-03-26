package com.xando.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.auth.navigation.internal.ForgotPasswordKey
import com.xando.auth.navigation.internal.SignUpKey
import com.xando.auth.presentation.forgot_password.ForgotPasswordScreen
import com.xando.auth.presentation.login.LoginScreen
import com.xando.auth.presentation.sign_up.SignUpScreen
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.auth.LoginKey
import com.xando.navigation_api.features.home.HomeKey

/**
 * Регистрирует navigation entries для модуля auth
 */
internal fun EntryProviderScope<NavKey>.authEntryBuilder(navigationController: NavigationController) {
    entry<LoginKey> {
        LoginScreen(
            viewModel = hiltViewModel(),
            onSignUpClick = { _ ->
                navigationController.navigateTo(SignUpKey)
            },
            onForgotPasswordClick = {
                navigationController.navigateTo(ForgotPasswordKey)
            }
        )
    }

    entry<SignUpKey> {
        SignUpScreen(
            onSignUpSuccess = {
                navigationController.navigateAndClearStack(HomeKey)
            },
            onBackClick = {
                navigationController.navigateBack()
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