package com.xando.auth.navigation.internal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.xando.auth.ui.login.LoginScreen
import kotlinx.serialization.Serializable

/**@SelfDocumented*/
@Serializable
internal object LoginRoute

/**@SelfDocumented*/
internal fun NavGraphBuilder.loginScreen(onLogin: () -> Unit) {
    composable(LoginRoute::class) {
        LoginScreen(onLogin = onLogin)
    }
}