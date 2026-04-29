package com.xando.pets_list.presentation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel экрана списка питомцев.
 *
 * @param savedStateHandle Хранилище состояния экрана.
 * @param getPetListUseCase Сценарий получения списка питомцев.
 */
@HiltViewModel
class PetListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPetListUseCase: GetPetListUseCase
) : ViewModel() {

    companion object {
        private const val KEY_STATE = "PetListUiState"
    }

    private val _uiState = savedStateHandle.getMutableStateFlow(
        KEY_STATE,
        PetListUiState()
    )

    /**
     * Состояние экрана списка питомцев.
     */
    val uiState: StateFlow<PetListUiState> = _uiState.asStateFlow()

    private val _events = Channel<PetListEvent>(capacity = Channel.UNLIMITED)

    /**
     * Одноразовые события экрана списка питомцев.
     */
    val events: Flow<PetListEvent> = _events.receiveAsFlow()

    init {
        loadPets()
    }

    /**
     * Загружает список питомцев.
     */
    fun loadPets() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val pets = getPetListUseCase()
                _uiState.update { it.copy(pets = pets, isLoading = false) }
            } catch (ex: ApiException) {
                catchApiException(ex)
            } catch (th: Throwable) {
                ensureActive()
                _events.trySend(PetListEvent.ShowSnackbar(StayaSnackbarData.unknown()))
                Log.w("PetListViewModel", th)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Запускает обновление списка питомцев через pull-to-refresh.
     */
    fun onRefresh() {
        loadPets()
    }

    private fun catchApiException(ex: ApiException) {
        val message = when (ex) {
            is NetworkException -> StayaSnackbarData.noInternet()
            is ServerUnavailableException -> StayaSnackbarData.serverUnavailable()
            else -> {
                Log.w("PetListViewModel", ex)
                StayaSnackbarData.unknown()
            }
        }
        _events.trySend(PetListEvent.ShowSnackbar(message))
    }
}
