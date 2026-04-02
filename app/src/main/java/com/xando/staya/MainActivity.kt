package com.xando.staya

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.xando.navigation_api.EntryBuilder
import com.xando.staya.presentation.RootHost
import com.xando.staya.presentation.RootViewModel
import com.xando.staya.ui.theme.StayaTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**@SelfDocumented*/
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Set всех [EntryBuilder] из feature модулей.
     * Hilt автоматически собирает их через @IntoSet провайдеры.
     */
    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryBuilder>

    private val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Держим сплеш пока не будет информации о логине
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isAuthorized.value == null
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            // Пока не поддерживаем темную тему
            statusBarStyle = SystemBarStyle.light(scrim = Color.TRANSPARENT, darkScrim = Color.TRANSPARENT)
        )
        setContent {
            StayaTheme {
                RootHost(entryBuilders, viewModel = viewModel)
            }
        }
    }
}