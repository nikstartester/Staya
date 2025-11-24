package com.xando.staya.ui.navigation_bar_host

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

internal fun NavGraphBuilder.navigationBarHost() {
    composable(
        route = NavigationBarHosRoute::class,
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {
        val navController = rememberNavController()
        var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.MAP) }

        AppCloseBackHandler()

        Scaffold(
            bottomBar = {
                NavigationBar {
                    AppDestinations.entries.forEach {
                        NavigationBarItem(
                            icon = { Icon(it.icon, contentDescription = it.label) },
                            label = { Text(it.label) },
                            selected = it == currentDestination,
                            onClick = {
                                navController.navigate(it.route) {
                                    popUpTo(0) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                currentDestination = it
                            }
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = MapRoute,
                modifier = Modifier.padding(padding)
            ) {
                mapScreen()
                myPetsScreen()
                profileScreen()
            }
        }
    }
}

@Composable
private fun AppCloseBackHandler() {
    val context = LocalContext.current
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            (context as? ComponentActivity)?.finish()
        } else {
            Toast.makeText(
                context,
                "Нажмите ещё раз для выхода",
                Toast.LENGTH_SHORT
            ).show()
            backPressedTime = currentTime
        }
    }
}

@Serializable
internal object NavigationBarHosRoute

private enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val route: Any
) {
    MAP("Map", Icons.Default.Home, MapRoute),
    PETS("My Pets", Icons.Default.Star, MyPetsRoute),
    PROFILE("Profile", Icons.Default.Person, PurchaseListRoute),
}

//region Удалить после реализации
@Serializable
private object MapRoute

@Serializable
private object MyPetsRoute

@Serializable
private object PurchaseListRoute

fun NavGraphBuilder.mapScreen() {
    composable(route = MapRoute::class) {
        TestScreen(AppDestinations.MAP.label)
    }
}

fun NavGraphBuilder.myPetsScreen() {
    composable(route = MyPetsRoute::class) {
        TestScreen(AppDestinations.PETS.label)
    }
}

fun NavGraphBuilder.profileScreen() {
    composable(route = PurchaseListRoute::class) {
        TestScreen(AppDestinations.PROFILE.label)
    }
}

@Composable
fun TestScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}
//endregion
