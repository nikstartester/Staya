package com.xando.auth.ui.login

import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.content.edit

/**@SelfDocumented*/
@Composable
internal fun LoginScreen(onLogin: () -> Unit) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Button(
            onClick = {
                PreferenceManager.getDefaultSharedPreferences(context).edit { putBoolean("IS_LOGIN", true) }
                onLogin()
            },
        ) {
            Text(
                text = "LogIn",
                fontSize = 40.sp,
                style = TextStyle.Default
            )
        }
    }
}
