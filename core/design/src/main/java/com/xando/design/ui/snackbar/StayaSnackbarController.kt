package com.xando.design.ui.snackbar

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Контроллер для показа снекбаров в приложении.
 *
 * Предоставляется через [LocalSnackbarController] и доступен
 * в любом Composable внутри [CompositionLocalProvider][androidx.compose.runtime.CompositionLocalProvider].
 *
 * @see LocalSnackbarController
 * @see rememberStayaSnackbarController
 */
interface StayaSnackbarController {

    /**
     * Показывает снекбар с переданными данными.
     * Если в момент вызова отображается другой снекбар — он будет отменён.
     */
    fun show(data: StayaSnackbarData)
}

/**
 * [CompositionLocal][androidx.compose.runtime.CompositionLocal] для доступа к [StayaSnackbarController].
 * Должен быть предоставлен в корневом Composable через
 * [CompositionLocalProvider][androidx.compose.runtime.CompositionLocalProvider].
 */
val LocalSnackbarController = staticCompositionLocalOf<StayaSnackbarController> {
    error("StayaSnackbarController not provided")
}