package com.xando.core.common.search

import java.text.Normalizer

/**
 * Строит индекс для поиска по названиям элементов списка.
 *
 * @param label Отдаёт название элемента - строку, по которой идёт поиск.
 */
fun <T> List<T>.buildSearchIndex(label: (T) -> String): LabelSearchIndex<T> =
    LabelSearchIndex(
        map { item ->
            val words = label(item).toSearchWords()
            SearchEntry(item = item, words = words, text = words.joinToString(WORD_SEPARATOR))
        }
    )

/**
 * Поиск по названиям элементов.
 *
 * Названия разбираются один раз при создании индекса, поэтому запрос сверяется с готовым разбором,
 * а не с исходными строками. Создаётся через [buildSearchIndex].
 */
class LabelSearchIndex<T> internal constructor(private val entries: List<SearchEntry<T>>) {

    /**
     * Элементы, подходящие под запрос [query]; более точные совпадения идут первыми.
     *
     * Запрос разбивается на слова, и каждое слово ищется по названию отдельно, в любом порядке:
     * «такса длин» находит «Такса кроличья длинношёрстная», хотя такой подстроки в названии нет.
     * Регистр, «ё» и диакритика не учитываются, поэтому «боксер» находит «Боксёр».
     *
     * Порядок выдачи: сначала названия, начинающиеся с запроса целиком, затем те, где слова запроса
     * стоят в начале слов названия, и последними - совпадения внутри слов. Внутри группы сохраняется
     * порядок исходного списка.
     *
     * @return Пустой список, если в запросе нет ни одной буквы или цифры.
     */
    fun search(query: String): List<T> {
        val tokens = query.toSearchWords()
        if (tokens.isEmpty()) return emptyList()

        val text = tokens.joinToString(WORD_SEPARATOR)
        return entries
            .mapNotNull { entry -> entry.rank(tokens, text)?.let { rank -> entry.item to rank } }
            .sortedBy { (_, rank) -> rank }
            .map { (item, _) -> item }
    }
}

/**
 * Название элемента, разобранное для поиска.
 *
 * @property item Элемент, которому принадлежит название.
 * @property words Слова названия без регистра, диакритики и знаков препинания.
 * @property text Те же слова через пробел - по ним проверяется совпадение с начала названия.
 */
internal class SearchEntry<T>(
    val item: T,
    private val words: List<String>,
    private val text: String,
) {

    /**
     * Насколько точно название совпало с запросом: чем меньше число, тем выше место в выдаче.
     *
     * @param tokens Слова запроса.
     * @param text Слова запроса через пробел.
     * @return `null`, если название не подходит под запрос.
     */
    fun rank(tokens: List<String>, text: String): Int? = when {
        this.text.startsWith(text) -> RANK_LABEL_PREFIX
        tokens.all { token -> words.any { it.startsWith(token) } } -> RANK_WORD_PREFIX
        tokens.all { token -> words.any { it.contains(token) } } -> RANK_INSIDE_WORD
        else -> null
    }
}

/**
 * Разбивает строку на слова для поиска: убирает регистр, диакритику и знаки препинания.
 *
 * Разложение по [Normalizer.Form.NFD] отделяет диакритику от буквы, а [DIACRITICS] её убирает: так
 * «ё» сводится к «е», и запрос совпадает с названием независимо от того, как его набрали.
 */
private fun String.toSearchWords(): List<String> =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace(DIACRITICS, "")
        .lowercase()
        .split(NON_WORD_CHARS)
        .filter { it.isNotEmpty() }

/** Диакритические знаки, отделённые от букв разложением по [Normalizer.Form.NFD]. */
private val DIACRITICS = Regex("\\p{Mn}+")

/** Всё, что не буква и не цифра: дефисы и скобки в названиях разделяют слова. */
private val NON_WORD_CHARS = Regex("[^\\p{L}\\p{N}]+")

/** Разделитель слов в разобранном названии и в запросе. */
private const val WORD_SEPARATOR = " "

/** Название целиком начинается с запроса. */
private const val RANK_LABEL_PREFIX = 0

/** Каждое слово запроса стоит в начале какого-то слова названия. */
private const val RANK_WORD_PREFIX = 1

/** Каждое слово запроса встречается внутри какого-то слова названия. */
private const val RANK_INSIDE_WORD = 2
