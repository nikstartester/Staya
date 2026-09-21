package com.xando.pet_interests_picker.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.core.models.pet.PetInterest
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.components.chip.StayaChip
import com.xando.design.ui.components.search.StayaSearchPanel
import com.xando.design.ui.snackbar.LocalSnackbarController
import com.xando.design.ui.snackbar.SnackbarType
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.pet_interests_picker.R
import com.xando.core.design.R as RDesign

/**
 * Экран выбора интересов питомца.
 *
 * Во время поиска группы скрываются: результаты показываются плоским списком по точности совпадения.
 *
 * @param viewModel ViewModel экрана.
 * @param onBackClick Обработчик нажатия «Назад».
 * @param onConfirm Обработчик подтверждения выбора.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InterestsPickerScreen(
    viewModel: InterestsPickerViewModel,
    onBackClick: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackbarController = LocalSnackbarController.current
    val limitReachedMessage =
        pluralStringResource(R.plurals.pet_interests_picker_limit_reached, state.maxSelection, state.maxSelection)

    LaunchedEffect(limitReachedMessage) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    InterestsPickerEvent.LimitReached -> {
                        snackbarController.show(
                            StayaSnackbarData(type = SnackbarType.ACCENT, message = limitReachedMessage)
                        )
                    }
                }
            }
    }

    val catalog = rememberInterestCatalog()
    val query = state.query.trim()
    val searchResults = remember(catalog, query) { catalog.search(query) }

    val groupsListState = rememberLazyListState()
    // Своё состояние прокрутки на каждый запрос: результаты нового поиска открываются сверху, а не
    // там, где осталась прокрутка предыдущих.
    val searchListState = key(query) { rememberLazyListState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.pet_interests_picker_title)) },
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
                placeholder = stringResource(R.string.pet_interests_picker_search_placeholder)
            )

            val selectionInfo = if (state.selected.isEmpty()) {
                pluralStringResource(R.plurals.pet_interests_picker_limit_hint, state.maxSelection, state.maxSelection)
            } else {
                stringResource(R.string.pet_interests_picker_selected_count, state.selected.size, state.maxSelection)
            }
            Text(
                text = selectionInfo,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.extendedColors.unaccentedTextColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.selected, key = { it.name }) { interest ->
                    StayaChip(
                        text = catalog.label(interest),
                        selected = true,
                        onClick = { viewModel.toggleInterest(interest) },
                        modifier = Modifier
                            .animateItem()
                            .padding(vertical = 4.dp),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(RDesign.drawable.design_ic_close_24px),
                                contentDescription = null,
                                tint = MaterialTheme.extendedColors.secondaryBackgroundTextColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    state = if (query.isEmpty()) groupsListState else searchListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = DoneButtonAreaHeight)
                ) {
                    if (query.isEmpty()) {
                        catalog.groups.forEach { group ->
                            item(key = group.label) {
                                Text(
                                    text = group.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.extendedColors.primaryTextColor,
                                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                                )
                            }
                            interestItems(
                                interests = group.items,
                                selected = state.selected,
                                isLimitReached = state.isLimitReached,
                                onToggle = viewModel::toggleInterest,
                            )
                        }
                    } else {
                        interestItems(
                            interests = searchResults,
                            selected = state.selected,
                            isLimitReached = state.isLimitReached,
                            onToggle = viewModel::toggleInterest,
                        )
                    }
                }

                StayaButton(
                    text = stringResource(R.string.pet_interests_picker_done),
                    onClick = {
                        viewModel.confirm()
                        onConfirm()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}

/** Высота области снизу списка, занятой плавающей кнопкой «Готово» вместе с её отступами. */
private val DoneButtonAreaHeight = 72.dp

/**
 * Добавляет в список строки интересов [interests].
 *
 * @param selected Выбранные интересы.
 * @param isLimitReached Признак того, что выбрано максимально допустимое количество интересов.
 * @param onToggle Переключает выбор интереса.
 */
private fun LazyListScope.interestItems(
    interests: List<InterestItem>,
    selected: List<PetInterest>,
    isLimitReached: Boolean,
    onToggle: (PetInterest) -> Unit,
) {
    items(interests, key = { it.interest.name }) { item ->
        val isSelected = item.interest in selected
        InterestRow(
            name = item.label,
            isSelected = isSelected,
            enabled = isSelected || !isLimitReached,
            onClick = { onToggle(item.interest) }
        )
    }
}

/**
 * Строка интереса с чекбоксом. Нажатие принимает вся строка, в том числе при `enabled = false`:
 * недоступный интерес лишь выглядит серым, а переключать его или объяснять лимит, решает ViewModel.
 */
@Composable
private fun InterestRow(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .toggleable(value = isSelected, role = Role.Checkbox, onValueChange = { onClick() })
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isSelected, onCheckedChange = null, enabled = enabled)
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) Color.Unspecified else MaterialTheme.extendedColors.unaccentedTextColor,
            modifier = Modifier.weight(1f)
        )
    }
}
