package com.xando.staya.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.data.auth.AuthStateProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**@SelfDocumented*/
@HiltViewModel
internal class RootViewModel @Inject constructor(
    authStateProvider: AuthStateProvider,
) : ViewModel() {

    private var previousAuthState: Boolean? = null

    private val _events = Channel<RootEvent>(capacity = Channel.BUFFERED)

    /**@SelfDocumented*/
    val events: Flow<RootEvent> = _events.receiveAsFlow()

    /** Состояние авторизации. `null` — начальная загрузка, `true` — авторизован, `false` — нет. */
    val isAuthorized: StateFlow<Boolean?> = authStateProvider.isAuthorized
        .onEach { isAuthorized ->
            val previous = previousAuthState
            previousAuthState = isAuthorized

            if (previous != null && previous != isAuthorized) {
                _events.trySend(RootEvent.AuthStateChanged(isAuthorized))
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}

/**@SelfDocumented*/
internal sealed interface RootEvent {

    /**
     * Изменилось состояние авторизации.
     */
    data class AuthStateChanged(val isAuthorized: Boolean) : RootEvent
}
