package com.xando.design.ui.snackbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import com.xando.core.design.R

/**
 * Данные для отображения снекбара.
 *
 * Реализует [SnackbarVisuals] для совместимости с [SnackbarHostState][androidx.compose.material3.SnackbarHostState].
 * Для стандартных ошибок используйте методы из [companion object][Companion].
 *
 * @param type тип снекбара, определяющий цвет фона.
 * @param message текст сообщения. Если пуст — используется [messageResId].
 * @param messageResId строковый ресурс сообщения. Игнорируется, если [message] не пуст.
 * @param iconRes иконка, отображаемая слева от текста.
 */
data class StayaSnackbarData(
    val type: SnackbarType,
    override val message: String = "",
    @param:StringRes val messageResId: Int = 0,
    @param:DrawableRes val iconRes: Int? = null
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false
    override val duration: SnackbarDuration = SnackbarDuration.Short

    companion object {

        /** Ошибка отсутствия сети. */
        fun noInternet() = StayaSnackbarData(
            type = SnackbarType.ERROR,
            messageResId = R.string.design_error_no_internet,
            iconRes = R.drawable.design_ic_wifi_off_24px
        )

        /** Ошибка недоступности сервера. */
        fun serverUnavailable() = StayaSnackbarData(
            type = SnackbarType.ERROR,
            messageResId = R.string.design_error_server_unavailable,
            iconRes = R.drawable.design_ic_cloud_off_24px
        )

        /** Ошибка превышения лимита запросов. */
        fun rateLimited() = StayaSnackbarData(
            type = SnackbarType.ERROR,
            messageResId = R.string.design_error_rate_limit,
            iconRes = R.drawable.design_ic_do_not_touch_24px
        )

        /** Неизвестная ошибка. */
        fun unknown() = StayaSnackbarData(
            type = SnackbarType.ERROR,
            messageResId = R.string.design_error_unknown,
            iconRes = R.drawable.design_ic_error_24px
        )
    }
}

/**
 * Перечисление типов снекбаров
 */
enum class SnackbarType {

    /**
     * Информер для ошибки
     */
    ERROR,

    /**
     * Информер об успехе
     */
    SUCCESS,

    /**
     * Акцентный-информер, цвета темы приложения
     */
    ACCENT
}