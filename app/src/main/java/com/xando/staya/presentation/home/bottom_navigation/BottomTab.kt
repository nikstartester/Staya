package com.xando.staya.presentation.home.bottom_navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.xando.staya.R
import com.xando.core.design.R as RDesign

/**
 * Табы навигации.
 */
internal enum class BottomTab(@param:StringRes val labelRes: Int, @param:DrawableRes val iconRes: Int) {

    /**
     * Карта (Прогулка)
     */
    MAP(R.string.app_bottom_tab_map, RDesign.drawable.design_map_24px),

    /**
     * Питомцы
     */
    PETS(R.string.app_bottom_tab_pets, RDesign.drawable.design_pets_24px),

    /**
     * Сообщения
     */
    MESSAGES(R.string.app_bottom_tab_messages, RDesign.drawable.design_chat_24px),

    /**
     * Профиль
     */
    PROFILE(R.string.app_bottom_tab_profile, RDesign.drawable.design_settings_account_box_24px)
}