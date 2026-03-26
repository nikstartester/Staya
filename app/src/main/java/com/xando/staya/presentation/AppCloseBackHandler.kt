package com.xando.staya.presentation

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

private const val CLOSE_APP_INTERVAL_MS = 2000

@Composable
internal fun AppCloseBackHandler() {
    val context = LocalContext.current
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < CLOSE_APP_INTERVAL_MS) {
            (context as? ComponentActivity)?.finish()
        } else {
            Toast.makeText(
                context,
                // TODO: в ресурсы
                "Нажмите ещё раз для выхода",
                Toast.LENGTH_SHORT
            ).show()
            backPressedTime = currentTime
        }
    }
}