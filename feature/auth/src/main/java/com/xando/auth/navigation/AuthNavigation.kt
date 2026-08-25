package com.xando.auth.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.xando.auth.forgot_password.ForgotPasswordScreen
import com.xando.auth.login.presentation.LoginScreen
import com.xando.auth.navigation.internal.ForgotPasswordKey
import com.xando.auth.navigation.internal.SignUpKey
import com.xando.auth.sign_up.SignUpScreen
import com.xando.navigation_api.NavigationController
import com.xando.navigation_api.features.auth.LoginKey

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