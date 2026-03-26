package com.xando.auth.presentation.sign_up.pages.about

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.xando.auth.presentation.sign_up.components.BottomSectionAction
import com.xando.auth.presentation.sign_up.components.SignUpPage
import com.xando.design.ui.components.text_field.StayaOutlinedTextField
import com.xando.feature.auth.R

/**
 * Экран ввода информации о себе - второй шаг флоу регистрации.
 */
@Composable
internal fun SignUpAboutScreen(onContinue: () -> Unit) {
    val viewModel = hiltViewModel<SignUpAboutViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val action = if (uiState.description.isNotBlank()) BottomSectionAction.CONTINUE else BottomSectionAction.SKIP

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(Unit) {
        viewModel.events
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    SignUpAboutEvent.NavigateNext -> onContinue()
                }
            }
    }

    SignUpPage(
        title = stringResource(R.string.auth_sign_up_about_title),
        onContinue = { viewModel.onContinueClick() },
        currentStep = 2,
        action = action,
    ) {
        StayaOutlinedTextField(
            value = uiState.description,
            onValueChange = { viewModel.updateDescription(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.auth_sign_up_about_placeholder),
            maxLines = 6,
            minLines = 6,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            )
        )
    }
}
