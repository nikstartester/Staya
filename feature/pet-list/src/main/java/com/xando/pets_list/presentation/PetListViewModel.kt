package com.xando.pets_list.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xando.core.api_models.ApiException
import com.xando.core.api_models.NetworkException
import com.xando.core.api_models.ServerUnavailableException
import com.xando.design.ui.snackbar.StayaSnackbarData
import com.xando.pets_list.domain.GetPetListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel экрана списка питомцев.
 *
 * @param getPetListUseCase Сценарий получения списка питомцев.
 */
@HiltViewModel
class PetListViewModel @Inject constructor(
    private val getPetListUseCase: GetPetListUseCase,
) : ViewModel() {

    private companion object {
        const val TAG = "PetListViewModel"

        /** Сколько держать подписку на базу после ухода экрана - переживает поворот без перезапроса. */
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }

    private val isRefreshing = MutableStateFlow(false)

    /**
     * Состояние экрана списка питомцев.
     */
    val uiState: StateFlow<PetListUiState> = combine(
        getPetListUseCase(),
        isRefreshing,
    ) { pets, refreshing ->
        PetListUiState(
            pets = pets,
            isLoading = refreshing,
            isEmptyStubVisible = pets.isEmpty() && !refreshing,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = PetListUiState(isLoading = true),
    )

    private val _events = Channel<PetListEvent>(capacity = Channel.UNLIMITED)

    /**
     * Одноразовые события экрана списка питомцев.
     */
    val events: Flow<PetListEvent> = _events.receiveAsFlow()

    init {
        refresh()
    }

    /**
     * Запускает обновление списка питомцев через pull-to-refresh.
     */
    fun onRefresh() {
        refresh()
    }

    private fun refresh() {
        if (isRefreshing.value) return

        isRefreshing.value = true

        viewModelScope.launch {
            try {
                getPetListUseCase.refresh()
            } catch (ex: ApiException) {
                catchApiException(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(PetListEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                Log.w(TAG, th)
            } finally {
                isRefreshing.value = false
            }
        }
    }

    private fun catchApiException(ex: ApiException) {
        val message = when (ex) {
            is NetworkException -> StayaSnackbarData.noInternet()
            is ServerUnavailableException -> StayaSnackbarData.serverUnavailable()
            else -> {
                Log.w(TAG, ex)
                StayaSnackbarData.unknown()
            }
        }
        _events.trySend(PetListEvent.ShowSnackbar(message))
    }
}
