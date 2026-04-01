package com.xando.design.ui.snackbar

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.xando.design.ui.snackbar.impl.StayaSnackbarControllerImpl

/**
 * Создаёт и запоминает [StayaSnackbarController], привязанный к [hostState].
 *
 * @see LocalSnackbarController
 */
@Composable
fun rememberStayaSnackbarController(hostState: SnackbarHostState): StayaSnackbarController {
    val scope = rememberCoroutineScope()
    return remember(hostState, scope) {
        StayaSnackbarControllerImpl(hostState, scope)
    }
}