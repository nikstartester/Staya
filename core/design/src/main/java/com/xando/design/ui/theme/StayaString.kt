package com.xando.design.ui.theme

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Обёртка над строковым ресурсом или готовым текстом.
 * Позволяет передавать строки из ViewModel в UI без привязки к [Context].
 */
sealed class StayaString : Parcelable, Serializable {

    /**
     * Строка из ресурсов.
     *
     * @property resId Идентификатор строкового ресурса.
     * @property args Аргументы для форматирования строки.
     */
    @Parcelize
    data class Res(
        @param:StringRes val resId: Int,
        val args: Array<out Serializable> = emptyArray(),
    ) : StayaString() {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Res) return false
            return resId == other.resId && args.contentEquals(other.args)
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    /** Готовая строка. */
    @Parcelize
    data class Value(val text: String) : StayaString()

    companion object {
        /**
         * Пустая строка.
         */
        val EMPTY = Value("")
    }
}

/** Возвращает строку в Composable-контексте. */
@Composable
fun StayaString.getString(): String = when (this) {
    is StayaString.Res -> stringResource(resId, *args)
    is StayaString.Value -> text
}

/** Возвращает строку через [Context]. */
fun StayaString.getString(context: Context): String = when (this) {
    is StayaString.Res -> context.getString(resId, *args)
    is StayaString.Value -> text
}
