package com.xando.pet_form.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.core.models.pet.PetBreed
import com.xando.core.models.pet.PetInterest
import com.xando.core.models.pet.PetSex
import com.xando.design.ui.components.button.StayaButton
import com.xando.design.ui.components.image_crop.StayaCircleCropDialog
import com.xando.design.ui.components.photo_picker.StayaPhotoPicker
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.snackbar.LocalSnackbarController
import com.xando.design.ui.theme.getString
import com.xando.feature.pet_form.R
import com.xando.pet_form.presentation.components.AboutSection
import com.xando.pet_form.presentation.components.BirthDatePickerDialog
import com.xando.pet_form.presentation.components.BirthDateRow
import com.xando.pet_form.presentation.components.BreedRow
import com.xando.pet_form.presentation.components.InterestsSection
import com.xando.pet_form.presentation.components.SexPicker
import com.xando.core.design.R as RDesign

private const val NAME_MAX_LENGTH = 64

/** Отступ между соседними полями формы. */
private val FieldSpacing = 8.dp

/** Отступ между смысловыми блоками формы. */
private val SectionSpacing = 20.dp

/**
 * Экран создания/редактирования питомца.
 *
 * @param viewModel ViewModel экрана.
 * @param onBackClick Обработчик нажатия «Назад».
 * @param onPickBreed Открывает экран выбора породы.
 * @param onPickInterests Открывает экран выбора интересов.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PetFormScreen(
    viewModel: PetFormViewModel,
    onBackClick: () -> Unit,
    onPickBreed: (currentBreed: PetBreed?) -> Unit,
    onPickInterests: (currentInterests: List<PetInterest>, maxSelection: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackbarController = LocalSnackbarController.current

    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is PetFormEvent.ShowSnackbar -> snackbarController.show(event.snackbarData)
                    PetFormEvent.NavigateBack -> onBackClick()
                }
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isEditMode) R.string.pet_form_title_edit else R.string.pet_form_title_create
                        )
                    )
                },
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
        if (state.isLoading) {
            LoadingState(paddingValues)
        } else {
            EditState(
                paddingValues = paddingValues,
                state = state,
                onPhotoSelected = viewModel::onPhotoPicked,
                onPhotoRemove = viewModel::removePhoto,
                onNameChange = viewModel::updateName,
                onPickBreedClick = { onPickBreed(state.breed) },
                onWeightChange = viewModel::updateWeight,
                onBirthDateChange = viewModel::updateBirthDate,
                onCalendarClick = viewModel::openDatePicker,
                onSexSelect = viewModel::selectSex,
                onInterestToggle = viewModel::toggleInterest,
                onPickInterests = { onPickInterests(state.chosenInterests, state.maxInterests) },
                onAboutChange = viewModel::updateAbout,
                onSaveClick = viewModel::save
            )
        }
    }

    if (state.showDatePicker) {
        BirthDatePickerDialog(
            initialMillis = state.birthDateMillis,
            onConfirm = viewModel::selectBirthDate,
            onDismiss = viewModel::dismissDatePicker,
        )
    }

    state.cropPhotoUri?.let { cropPhotoUri ->
        StayaCircleCropDialog(
            model = cropPhotoUri,
            onCropConfirmed = viewModel::onCropConfirmed,
            onDismiss = viewModel::onCropDismissed,
        )
    }
}

@Composable
private fun LoadingState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EditState(
    paddingValues: PaddingValues,
    state: PetFormUiState,
    onPhotoSelected: (Uri) -> Unit,
    onPhotoRemove: () -> Unit,
    onNameChange: (String) -> Unit,
    onPickBreedClick: () -> Unit,
    onWeightChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onCalendarClick: () -> Unit,
    onSexSelect: (PetSex) -> Unit,
    onInterestToggle: (PetInterest) -> Unit,
    onPickInterests: () -> Unit,
    onAboutChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        StayaPhotoPicker(
            model = state.photoUri ?: state.existingPhotoUrl,
            contentDescription = stringResource(R.string.pet_form_photo_content_description),
            onPhotoSelected = onPhotoSelected,
            onRemoveClick = onPhotoRemove,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(FieldSpacing))

        StayaOutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = stringResource(R.string.pet_form_name_label),
            singleLine = true,
            maxLength = NAME_MAX_LENGTH,
            errorText = state.nameError?.getString(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
        )

        Spacer(modifier = Modifier.height(FieldSpacing))

        BreedRow(
            breed = state.breed,
            error = state.breedError,
            onClick = onPickBreedClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(FieldSpacing))

        StayaOutlinedTextField(
            value = state.weight,
            onValueChange = onWeightChange,
            label = stringResource(R.string.pet_form_weight_label),
            placeholder = stringResource(R.string.pet_form_weight_placeholder),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            errorText = state.weightError?.getString(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(FieldSpacing))

        BirthDateRow(
            value = state.birthDate,
            error = state.birthDateError,
            onValueChange = onBirthDateChange,
            onCalendarClick = onCalendarClick,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SectionSpacing))

        SexPicker(
            selected = state.sex,
            error = state.sexError,
            onSelect = onSexSelect,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SectionSpacing))

        InterestsSection(
            interests = state.interestChips,
            chosenInterests = state.chosenInterests,
            maxInterests = state.maxInterests,
            onToggle = onInterestToggle,
            onAddClick = onPickInterests,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SectionSpacing))

        AboutSection(
            value = state.about,
            onValueChange = onAboutChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(SectionSpacing))

        StayaButton(
            text = stringResource(
                if (state.isEditMode) R.string.pet_form_save_edit else R.string.pet_form_save_create
            ),
            onClick = onSaveClick,
            isLoading = state.isSaving,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


