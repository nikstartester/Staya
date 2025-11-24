package com.xando.auth.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.xando.auth.navigation.internal.LoginRoute
import com.xando.auth.navigation.internal.loginScreen
import kotlinx.serialization.Serializable

/**
 * Маркер-объект, определяющий вложенный граф навигации для процесса авторизации
 */
@Serializable
object LoginGraphRoute

/**
 * Переход к флоу авторизации
 */
fun NavController.navigateToLoginScreen() {
    navigate(LoginGraphRoute)
}

/**
 * Регистрация графа авторизации в общем NavHost
 */
fun NavGraphBuilder.loginGraph(onLogin: () -> Unit) {
    navigation(route = LoginGraphRoute::class, startDestination = LoginRoute) {
        loginScreen(onLogin = onLogin)
    }
}