package com.xando.auth.ui.sign_up.pages.introduction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.xando.auth.ui.sign_up.components.BottomSectionAction
import com.xando.auth.ui.sign_up.components.SignUpPage
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.design.ui.theme.extendedColors
import com.xando.feature.auth.R
import com.xando.core.design.R as RDesign

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
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                SignUpIntroductionViewModel.Event.NavigateNext -> onContinue()
            }
        }
    }

    SignUpPage(
        title = stringResource(R.string.auth_sign_up_introduction_title),
        onContinue = { viewModel.onContinueClick() },
        currentStep = 1,
        action = BottomSectionAction.CONTINUE,
    ) {
        PhotoPicker(
            photoUri = uiState.photoUri,
            onPhotoSelected = { viewModel.updatePhotoUri(it) },
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        StayaOutlinedTextField(
            value = uiState.firstName,
            onValueChange = { viewModel.updateFirstName(it) },
            label = stringResource(R.string.auth_sign_up_introduction_first_name_label),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            errorText = uiState.firstNameError,
        )

        Spacer(modifier = Modifier.height(16.dp))

        StayaOutlinedTextField(
            value = uiState.lastName,
            onValueChange = { viewModel.updateLastName(it) },
            label = stringResource(R.string.auth_sign_up_introduction_last_name_label),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            errorText = uiState.lastNameError,
        )
    }
}

/**
 * Выбор и отображение фотографии профиля на первом шаге регистрации.
 */
@Composable
private fun PhotoPicker(
    photoUri: String?,
    onPhotoSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(contract = PickVisualMedia()) { uri: Uri? ->
        if (uri != null) onPhotoSelected(uri.toString())
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                photoPickerLauncher.launch(PickVisualMediaRequest(mediaType = PickVisualMedia.ImageOnly))
            },
        contentAlignment = Alignment.Center
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = stringResource(R.string.auth_sign_up_introduction_photo_content_description),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        } else {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(RDesign.drawable.design_ic_photo_camera_24dp),
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.iconColor
                )
                Text(
                    text = stringResource(R.string.auth_sign_up_introduction_photo_title),
                    autoSize = TextAutoSize.StepBased(
                        minFontSize = 6.sp,
                        maxFontSize = MaterialTheme.typography.bodyMedium.fontSize
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    color = MaterialTheme.extendedColors.textColor
                )
            }
        }
    }
}
