package com.xando.staya.ui

import android.preference.PreferenceManager
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.xando.auth.navigation.LoginGraphRoute
import com.xando.auth.navigation.loginGraph
import com.xando.staya.ui.navigation_bar_host.NavigationBarHosRoute
import com.xando.staya.ui.navigation_bar_host.navigationBarHost

@Composable
fun RootHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = getStartDestination(),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            )
        }
    ) {
        loginGraph(onLogin = {
            navController.navigate(NavigationBarHosRoute) {
                popUpTo(0)
            }
        })
        navigationBarHost()
    }
}

@Composable
private fun getStartDestination(): Any = if (isLogin()) NavigationBarHosRoute else LoginGraphRoute

@Composable
private fun isLogin() =
    PreferenceManager.getDefaultSharedPreferences(LocalContext.current).getBoolean("IS_LOGIN", false)