package com.xando.staya.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.data.auth.AuthStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**@SelfDocumented*/
@HiltViewModel
class RootViewModel @Inject constructor(
    authStateProvider: AuthStateProvider,
) : ViewModel() {

    /** Состояние авторизации. `null` — начальная загрузка, `true` — авторизован, `false` — нет. */
    val isAuthorized: StateFlow<Boolean?> = authStateProvider.isAuthorized
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}