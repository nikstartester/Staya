package com.xando.auth.sign_up.pages.introduction

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.auth.sign_up.components.BottomSectionAction
import com.xando.auth.sign_up.components.SignUpPage
import com.xando.design.ui.components.image_crop.StayaCircleCropDialog
import com.xando.design.ui.components.photo_picker.StayaPhotoPicker
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.snackbar.LocalSnackbarController
import com.xando.feature.auth.R

private const val NAME_MAX_LENGTH = 64

/**
 * Экран знакомства с пользователем - первый шаг флоу регистрации.
 *
 * Позволяет указать имя, фамилию и опционально загрузить фото профиля.
 */
@Composable
internal fun SignUpIntroductionScreen(
    onContinue: () -> Unit,
) {
    val viewModel = hiltViewModel<SignUpIntroductionViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val snackbarController = LocalSnackbarController.current

    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    SignUpIntroductionEvent.NavigateNext -> onContinue()
                    is SignUpIntroductionEvent.ShowSnackbar -> snackbarController.show(event.snackbarData)
                }
            }
    }

    SignUpPage(
        title = stringResource(R.string.auth_sign_up_introduction_title),
        onContinue = { viewModel.onContinueClick() },
        currentStep = 1,
        action = BottomSectionAction.CONTINUE,
    ) {
        StayaPhotoPicker(
            model = uiState.photoUri,
            contentDescription = stringResource(R.string.auth_sign_up_introduction_photo_content_description),
            onPhotoSelected = { viewModel.onPhotoPicked(it) },
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        val lastNameFocusRequester = remember { FocusRequester() }

        StayaOutlinedTextField(
            value = uiState.firstName,
            onValueChange = { viewModel.updateFirstName(it) },
            label = stringResource(R.string.auth_sign_up_introduction_first_name_label),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLength = NAME_MAX_LENGTH,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { lastNameFocusRequester.requestFocus() }),
            errorText = uiState.firstNameError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        StayaOutlinedTextField(
            value = uiState.lastName,
            onValueChange = { viewModel.updateLastName(it) },
            label = stringResource(R.string.auth_sign_up_introduction_last_name_label),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(lastNameFocusRequester),
            singleLine = true,
            maxLength = NAME_MAX_LENGTH,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            errorText = uiState.lastNameError,
        )
    }

    uiState.cropPhotoUri?.let { cropPhotoUri ->
        StayaCircleCropDialog(
            model = cropPhotoUri,
            onCropConfirmed = { viewModel.onCropConfirmed(it) },
            onDismiss = { viewModel.onCropDismissed() },
        )
    }
}
