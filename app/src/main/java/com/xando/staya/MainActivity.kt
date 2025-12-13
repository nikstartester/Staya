package com.xando.staya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xando.navigation_api.EntryBuilder
import com.xando.staya.ui.RootHost
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StayaTheme {
                RootHost(entryBuilders)
            }
        }
    }
}