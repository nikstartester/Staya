package com.xando.pet_interests_picker.presentation

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import com.xando.core.common.search.buildSearchIndex
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetInterestGroup
import com.xando.core.pet_dictionary.labelRes

/**
 * Готовый к показу список интересов: группы с названиями на языке приложения и поиск по ним.
 *
 * @property groups Группы интересов в порядке показа.
 * @param labels Названия всех интересов.
 */
@Immutable
internal class InterestCatalog(
    val groups: List<InterestGroupItems>,
    private val labels: Map<PetInterest, String>,
) {

    /** Названия интересов, разобранные для поиска; порядок выдачи тот же, что у [groups]. */
    private val searchIndex = groups.flatMap { it.items }.buildSearchIndex { it.label }

    /** Название интереса [interest]. */
    fun label(interest: PetInterest): String = labels.getValue(interest)

    /**
     * Интересы, подходящие под запрос [query]. Более точные совпадения идут первыми.
     */
    fun search(query: String): List<InterestItem> = searchIndex.search(query)
}

/**
 * Группа интересов вместе с названиями.
 *
 * @property label Название группы.
 * @property items Интересы группы в порядке показа.
 */
@Immutable
internal data class InterestGroupItems(val label: String, val items: List<InterestItem>)

/**
 * Интерес вместе с его названием.
 *
 * @property interest Интерес.
 * @property label Название интереса.
 */
@Immutable
internal data class InterestItem(val interest: PetInterest, val label: String)

/**
 * Собирает список интересов для текущего языка приложения и запоминает его до смены языка.
 */
@Composable
internal fun rememberInterestCatalog(): InterestCatalog {
    val resources = LocalResources.current
    val locale = LocalConfiguration.current.locales[0]
    return remember(locale) { buildInterestCatalog(resources) }
}

private fun buildInterestCatalog(resources: Resources): InterestCatalog {
    val labels = PetInterest.entries.associateWith { resources.getString(it.labelRes) }
    val groups = PetInterestGroup.entries.map { group ->
        InterestGroupItems(
            label = resources.getString(group.labelRes),
            items = PetInterest.entries
                .filter { it.group == group }
                .map { InterestItem(it, labels.getValue(it)) },
        )
    }
    return InterestCatalog(groups = groups, labels = labels)
}
