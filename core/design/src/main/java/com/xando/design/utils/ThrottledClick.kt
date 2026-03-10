package com.xando.design.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Интервал защиты от двойного нажатия (мс)
 */
private const val DEFAULT_CLICK_DEBOUNCE_INTERVAL = 1000L

/**
 * Оборачивает [onClick] с защитой от двойного нажатия.
 * Повторные нажатия в течение [debounceInterval] мс игнорируются.
 */
@Composable
fun throttledClick(
    debounceInterval: Long = DEFAULT_CLICK_DEBOUNCE_INTERVAL,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTime = remember { 0L }
    return {
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= debounceInterval) {
            lastClickTime = now
            onClick()
        }
    }
}