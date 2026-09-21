package com.xando.pet_breed_picker.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xando.core.models.pet.PetBreed
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.components.search.StayaSearchPanel
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.pet_breed_picker.R
import kotlinx.coroutines.launch
import com.xando.core.design.R as RDesign

/**
 * Экран выбора породы питомца.
 *
 * Список разбит на секции - популярные породы и дальше по буквам, а справа есть алфавитный указатель.
 * Во время поиска секции и указатель скрываются: результаты показываются плоским списком.
 *
 * @param viewModel ViewModel экрана.
 * @param onBackClick Обработчик нажатия «Назад».
 * @param onConfirm Обработчик подтверждения выбора.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BreedPickerScreen(
    viewModel: BreedPickerViewModel,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val catalog = rememberBreedCatalog(stringResource(R.string.pet_breed_picker_popular))
    val query = state.query.trim()
    val searchResults = remember(catalog, query) { catalog.search(query) }
    val sectionsListState = rememberLazyListState()
    // Своё состояние прокрутки на каждый запрос: результаты нового поиска открываются сверху, а не
    // там, где осталась прокрутка предыдущих.
    val searchListState = key(query) { rememberLazyListState() }
    val coroutineScope = rememberCoroutineScope()

    // Буква секции, чей заголовок сейчас закреплён вверху списка. derivedStateOf нужен,
    // чтобы указатель перерисовывался на смене буквы, а не на каждом кадре прокрутки.
    val activeLetter by remember(catalog, sectionsListState) {
        derivedStateOf {
            val firstVisibleIndex = sectionsListState.firstVisibleItemIndex
            catalog.letterAnchors.entries.lastOrNull { it.value <= firstVisibleIndex }?.key
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.pet_breed_picker_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(RDesign.drawable.design_ic_arrow_back_24dp),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StayaSearchPanel(
                searchQuery = state.query,
                onValueChange = viewModel::setQuery,
                placeholder = stringResource(R.string.pet_breed_picker_search_placeholder)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    BreedSectionsList(
                        sections = catalog.sections,
                        selectedBreed = state.selected,
                        onBreedClick = viewModel::selectBreed,
                        listState = sectionsListState
                    )

                    AlphabetIndex(
                        letters = catalog.letters,
                        activeLetter = activeLetter,
                        onLetterSelected = { letter ->
                            val anchor = catalog.letterAnchors[letter] ?: return@AlphabetIndex
                            coroutineScope.launch { sectionsListState.scrollToItem(anchor) }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(bottom = ConfirmButtonAreaHeight)
                    )
                } else {
                    BreedSearchResultsList(
                        results = searchResults,
                        selectedBreed = state.selected,
                        onBreedClick = viewModel::selectBreed,
                        listState = searchListState
                    )
                }

                StayaButton(
                    text = stringResource(R.string.pet_breed_picker_confirm),
                    onClick = {
                        viewModel.confirm()
                        onConfirm()
                    },
                    enabled = state.selected != null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

/** Высота области снизу списка, занятой плавающей кнопкой подтверждения вместе с её отступами. */
private val ConfirmButtonAreaHeight = 72.dp

/** Ширина полосы алфавитного указателя: попасть пальцем и не закрыть собой названия пород. */
private val AlphabetIndexWidth = 24.dp

/** Во сколько раз увеличивается буква секции, закреплённой сейчас вверху списка. */
private const val ActiveLetterScale = 1.5f

/**
 * Породы секциями: популярные, дальше по буквам алфавита. Заголовок текущей секции закреплён вверху.
 *
 * @param sections Секции в порядке показа.
 * @param selectedBreed Выбранная порода.
 * @param onBreedClick Вызывается для нажатой породы.
 * @param listState Состояние прокрутки; по нему же считается буква алфавитного указателя.
 */
@Composable
private fun BreedSectionsList(
    sections: List<BreedSection>,
    selectedBreed: PetBreed?,
    onBreedClick: (PetBreed) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = ConfirmButtonAreaHeight)
    ) {
        sections.forEach { section ->
            stickyHeader(key = section.title) {
                BreedSectionHeader(title = section.title)
            }
            items(section.items, key = { "${section.title}_${it.breed.name}" }) { item ->
                BreedRow(
                    name = item.label,
                    isSelected = item.breed == selectedBreed,
                    onClick = { onBreedClick(item.breed) }
                )
            }
        }
    }
}

/**
 * Результаты поиска плоским списком: без секций и без алфавитного указателя.
 *
 * @param results Найденные породы в порядке показа.
 * @param selectedBreed Выбранная порода.
 * @param onBreedClick Вызывается для нажатой породы.
 * @param listState Состояние прокрутки.
 */
@Composable
private fun BreedSearchResultsList(
    results: List<BreedItem>,
    selectedBreed: PetBreed?,
    onBreedClick: (PetBreed) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = ConfirmButtonAreaHeight)
    ) {
        items(results, key = { it.breed.name }) { item ->
            BreedRow(
                name = item.label,
                isSelected = item.breed == selectedBreed,
                onClick = { onBreedClick(item.breed) }
            )
        }
    }
}

@Composable
private fun BreedSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.extendedColors.primaryTextColor,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun BreedRow(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp + AlphabetIndexWidth),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                painter = painterResource(RDesign.drawable.design_ic_check_24px),
                tint = MaterialTheme.extendedColors.primaryIconColor,
                contentDescription = null
            )
        }
    }
}

/**
 * Алфавитный указатель: вертикальная полоска букв, нажатие на букву перескакивает к её секции.
 *
 * Буква секции, закреплённой сейчас вверху списка, выделяется цветом и увеличивается: так видно, где
 * находишься, и во время обычной прокрутки, а не только после нажатия на указатель.
 *
 * @param letters Буквы в порядке показа.
 * @param activeLetter Буква закреплённой секции; `null`, пока список стоит на популярных породах.
 * @param onLetterSelected Вызывается для нажатой буквы.
 */
@Composable
private fun AlphabetIndex(
    letters: List<Char>,
    activeLetter: Char?,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (letters.isEmpty()) return

    LazyColumn(
        modifier = modifier
            .width(AlphabetIndexWidth)
    ) {
        items(letters, key = { it }) { letter ->
            val isActive = letter == activeLetter
            // Увеличение через graphicsLayer, а не через размер шрифта: масштаб не влияет на замер,
            // поэтому строки указателя не разъезжаются.
            val scale by animateFloatAsState(
                targetValue = if (isActive) ActiveLetterScale else 1f,
                label = "AlphabetIndexLetterScale"
            )
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (isActive) {
                    MaterialTheme.extendedColors.primaryTextColor
                } else {
                    MaterialTheme.extendedColors.unaccentedTextColor
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { onLetterSelected(letter) })
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }
    }
}
