package com.xando.design.ui.snackbar.impl

import androidx.compose.material3.SnackbarHostState
import com.xando.design.ui.snackbar.StayaSnackbarController
import com.xando.design.ui.snackbar.StayaSnackbarData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Реализация [StayaSnackbarController].
 * При вызове [show] отменяет предыдущий снекбар и показывает новый.
 */
internal class StayaSnackbarControllerImpl(
    private val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) : StayaSnackbarController {

    private var currentJob: Job? = null

    override fun show(data: StayaSnackbarData) {
        currentJob?.cancel()
        currentJob = scope.launch {
            hostState.showSnackbar(data)
        }
    }
}