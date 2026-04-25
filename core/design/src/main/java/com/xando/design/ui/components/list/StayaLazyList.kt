package com.xando.design.ui.components.list

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.xando.design.ui.theme.extendedColors

/**
 * Базовый список с автоматическим отображением состояний загрузки, пустого списка и pull-to-refresh.
 *
 * Обёртка над [LazyColumn], которая самостоятельно переключает между тремя состояниями:
 * загрузка ([LoadingState]), пустой список ([EmptyState]) и контент ([ListState]).
 * При переданном [onRefresh] оборачивает содержимое в [PullToRefreshBox].
 *
 * @param items список элементов для отображения
 * @param isLoadingState флаг состояния загрузки
 * @param isEmptyState флаг состояния пустого списка
 * @param stubTextRes ресурс строки для пустого списка
 * @param stubIconRes ресурс иконки для пустого списка (по умолчанию не отображается)
 * @param modifier модификатор корневого компонента
 * @param key кастомный генератор ключей для элементов ([LazyItemScope].items key)
 * @param contentPadding отступы содержимого списка
 * @param verticalArrangement вертикальное расстояние между элементами
 * @param onRefresh колбэк pull-to-refresh (null = без свайпа для обновления)
 * @param itemContent контент для каждого элемента списка
 */
@Composable
fun <T : Any> StayaLazyList(
    items: List<T>,
    isLoadingState: Boolean,
    isEmptyState: Boolean,
    @StringRes stubTextRes: Int,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(12.dp),
    @DrawableRes stubIconRes: Int = -1,
    onRefresh: (() -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    if (onRefresh != null) {
        val refreshState = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = isLoadingState,
            onRefresh = { onRefresh.invoke() },
            state = refreshState,
            modifier = modifier.fillMaxSize()
        ) {
            if (isEmptyState) EmptyState(stubTextRes, stubIconRes)
            else ListState(items, key, contentPadding, verticalArrangement, itemContent)
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            when {
                isLoadingState -> LoadingState()
                isEmptyState -> EmptyState(stubTextRes, stubIconRes)
                else -> ListState(items, key, contentPadding, verticalArrangement, itemContent)
            }
        }
    }
}

@Composable
private fun EmptyState(@StringRes textRes: Int, @DrawableRes iconRes: Int = -1) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        if (iconRes != -1) {
            Icon(
                modifier = Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = MaterialTheme.extendedColors.placeholderIconColor
            )
        }

        Text(
            textAlign = TextAlign.Center,
            text = stringResource(textRes),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.extendedColors.placeholderTextColor,
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun <T : Any> ListState(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement
    ) {
        if (key != null) {
            items(items, key = key) { itemContent(it) }
        } else {
            items(items) { itemContent(it) }
        }
    }
}
