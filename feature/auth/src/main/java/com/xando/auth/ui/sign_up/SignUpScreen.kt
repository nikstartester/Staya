package com.xando.auth.ui.sign_up

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.xando.auth.ui.sign_up.navigation.internal.SignUpAboutKey
import com.xando.auth.ui.sign_up.navigation.internal.SignUpEmailPasswordKey
import com.xando.auth.ui.sign_up.navigation.internal.SignUpIntroductionKey
import com.xando.auth.ui.sign_up.navigation.internal.SignUpLoginKey
import com.xando.auth.ui.sign_up.pages.about.SignUpAboutScreen
import com.xando.auth.ui.sign_up.pages.email_password.SignUpEmailPasswordScreen
import com.xando.auth.ui.sign_up.pages.introduction.SignUpIntroductionScreen
import com.xando.auth.ui.sign_up.pages.login.SignUpLoginScreen
import com.xando.design.animations.rightInLeftOutTransition
import com.xando.design.animations.rightOutLeftInTransition
import com.xando.feature.auth.R
import com.xando.navigation_impl.rememberNavigationController
import com.xando.core.design.R as RDesign

/**
 * Экран регистрации нового пользователя.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val viewModel = hiltViewModel<SignUpScreenViewModel>()

    val backStack = rememberNavBackStack(SignUpIntroductionKey)
    val navigationController = rememberNavigationController(backStack)

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.auth_sign_up_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (navigationController.canNavigateBack()) navigationController.navigateBack()
                        else onBackClick()
                    }) {
                        Icon(
                            painter = painterResource(RDesign.drawable.design_ic_arrow_back_24dp),
                            contentDescription = stringResource(R.string.auth_sign_up_back_content_description)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LaunchedEffect(Unit) {
            viewModel.events
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is SignUpFlowEvent.SignUpCompleted -> onSignUpSuccess()
                    }
                }
        }

        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues),
            onBack = {
                navigationController.navigateBack()
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(removeViewModelStoreOnPop = { false })
            ),
            transitionSpec = { rightInLeftOutTransition() },
            popTransitionSpec = { rightOutLeftInTransition() },
            predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },

            entryProvider = entryProvider {
                val navigateNext: (navKey: NavKey) -> Unit = { navKey ->
                    viewModel.onContinueClick()
                    navigationController.navigateTo(navKey)
                }
                entry<SignUpIntroductionKey> {
                    SignUpIntroductionScreen(
                        onContinue = {
                            navigateNext(SignUpAboutKey)
                        }
                    )
                }
                entry<SignUpAboutKey> {
                    SignUpAboutScreen(
                        onContinue = {
                            navigateNext(SignUpLoginKey)
                        }
                    )
                }
                entry<SignUpLoginKey> {
                    SignUpLoginScreen(
                        onContinue = {
                            navigateNext(SignUpEmailPasswordKey)
                        }
                    )
                }
                entry<SignUpEmailPasswordKey> {
                    SignUpEmailPasswordScreen(
                        onContinue = {
                            viewModel.onFinalStepCompleted()
                        }
                    )
                }
            }
        )
    }
}