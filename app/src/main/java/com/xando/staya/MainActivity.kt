package com.xando.staya

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xando.staya.ui.RootHost
import com.xando.staya.ui.theme.StayaTheme

/**@SelfDocumented*/
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StayaTheme {
                RootHost()
            }
        }
    }
}