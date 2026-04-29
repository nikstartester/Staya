package com.xando.pets_list.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.xando.core.models.pet.PetSummary
import com.xando.design.ui.components.list.StayaLazyList
import com.xando.design.ui.snackbar.LocalSnackbarController
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.pet_list.R
import com.xando.core.design.R as RDesign

/**
 * Экран списка питомцев.
 *
 * @param viewModel ViewModel списка питомцев.
 * @param modifier Модификатор корневого компонента.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetListScreen(
    viewModel: PetListViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackbarController = LocalSnackbarController.current

    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is PetListEvent.ShowSnackbar -> {
                        snackbarController.show(event.snackbarData)
                    }
                }
            }
    }

    val petList = state.pets
    val isLoading = state.isLoading
    val isEmptyState = petList.isEmpty() && !isLoading

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        containerColor = if (petList.isNotEmpty()) {
            MaterialTheme.extendedColors.unaccentedBackgroundColor
        } else MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.pet_list_title)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Icon(
                    painter = painterResource(RDesign.drawable.design_ic_add_24px),
                    contentDescription = stringResource(R.string.pet_list_add_pet)
                )
            }
        }
    ) { paddingValues ->
        StayaLazyList(
            petList,
            isLoadingState = isLoading,
            isEmptyState = isEmptyState,
            modifier = Modifier.padding(paddingValues),
            stubTextRes = R.string.pet_list_empty_list_text,
            stubIconRes = R.drawable.pet_list_empty_list_placeholder,
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            onRefresh = viewModel::onRefresh
        ) { index, pet ->
            PetListItem(
                pet = pet,
                isFirst = index == 0,
                isLast = index == petList.lastIndex
            )
        }
    }
}

@Composable
private fun PetListItem(
    pet: PetSummary,
    isFirst: Boolean,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier.fillMaxWidth(),
        shape = petListItemShape(isFirst, isLast)
    ) {
        Row(
            modifier = Modifier
                .clickable { }
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                if (pet.photoUrl.isEmpty()) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(RDesign.drawable.design_ic_photo_camera_24dp),
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.iconColor
                    )
                } else {
                    AsyncImage(
                        model = pet.photoUrl,
                        fallback = painterResource(RDesign.drawable.design_ic_photo_camera_24dp),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = pet.name,
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
            )
        }
    }
}

private fun petListItemShape(
    isFirst: Boolean,
    isLast: Boolean
) = RoundedCornerShape(
    topStart = if (isFirst) 12.dp else 4.dp,
    topEnd = if (isFirst) 12.dp else 4.dp,
    bottomStart = if (isLast) 12.dp else 4.dp,
    bottomEnd = if (isLast) 12.dp else 4.dp,
)
