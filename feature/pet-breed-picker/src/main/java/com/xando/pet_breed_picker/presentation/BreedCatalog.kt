package com.xando.pet_breed_picker.presentation

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import com.xando.core.common.search.buildSearchIndex
import com.xando.core.models.pet.PetBreed
import com.xando.core.pet_dictionary.labelRes
import java.text.Collator
import java.util.Locale

/**
 * Готовый к показу список пород: порядок, разбиение на секции и данные для алфавитного указателя.
 *
 * Названия пород приходят из ресурсов, поэтому и сортировка, и разбиение по буквам зависят от языка
 * приложения: в русской локали «Бигль» и в английской «Beagle» стоят в разных местах алфавита.
 *
 * @property sections Секции в порядке показа: сначала популярные, дальше по алфавиту.
 * @property all Все породы, отсортированные по названию - по ним идёт поиск.
 * @property letterAnchors Индекс строки, с которой начинается секция буквы, для скролла из указателя.
 */
@Immutable
internal class BreedCatalog(
    val sections: List<BreedSection>,
    val all: List<BreedItem>,
    val letterAnchors: Map<Char, Int>,
) {

    /** Буквы алфавитного указателя в порядке показа. */
    val letters: List<Char> = letterAnchors.keys.toList()

    /** Названия пород, разобранные для поиска; порядок выдачи тот же, что у [all]. */
    private val searchIndex = all.buildSearchIndex { it.label }

    /**
     * Породы, подходящие под запрос [query]. Более точные совпадения идут первыми.
     */
    fun search(query: String): List<BreedItem> = searchIndex.search(query)
}

/**
 * Секция списка пород: либо популярные породы, либо породы на одну букву.
 *
 * @property title Заголовок секции.
 * @property letter Буква алфавитного указателя; `null` у секции популярных пород - её в указателе нет.
 * @property items Породы секции в порядке показа.
 */
@Immutable
internal data class BreedSection(val title: String, val letter: Char?, val items: List<BreedItem>)

/**
 * Порода вместе с её названием.
 *
 * @property breed Порода.
 * @property label Название породы.
 */
@Immutable
internal data class BreedItem(val breed: PetBreed, val label: String)

/**
 * Собирает список пород для текущего языка приложения и запоминает его до смены языка.
 * @param popularTitle Заголовок секции популярных пород.
 */
@Composable
internal fun rememberBreedCatalog(popularTitle: String): BreedCatalog {
    val resources = LocalResources.current
    val locale = LocalConfiguration.current.locales[0]
    return remember(locale, popularTitle) { buildBreedCatalog(resources, locale, popularTitle) }
}

private fun buildBreedCatalog(
    resources: Resources,
    locale: Locale,
    popularTitle: String,
): BreedCatalog {
    val collator = Collator.getInstance(locale)
    val labels = PetBreed.entries.associateWith { resources.getString(it.labelRes) }
    val all = labels
        .map { (breed, label) -> BreedItem(breed, label) }
        .sortedWith { first, second -> collator.compare(first.label, second.label) }

    val popular = BreedSection(
        title = popularTitle,
        letter = null,
        items = PetBreed.POPULAR.map { BreedItem(it, labels.getValue(it)) },
    )
    val byLetter = all
        .groupBy { it.label.first().uppercaseChar() }
        .map { (letter, items) -> BreedSection(letter.toString(), letter, items) }

    val sections = listOf(popular) + byLetter
    val letterAnchors = LinkedHashMap<Char, Int>()
    var rowIndex = 0
    sections.forEach { section ->
        section.letter?.let { letterAnchors[it] = rowIndex }
        rowIndex += SECTION_HEADER_ROWS + section.items.size
    }

    return BreedCatalog(sections = sections, all = all, letterAnchors = letterAnchors)
}

/** Заголовок секции - такая же строка списка, как и порода, и её нужно учитывать при подсчёте индексов. */
private const val SECTION_HEADER_ROWS = 1
