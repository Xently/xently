package co.ke.xently.features.location.picker.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.libraries.location.tracker.domain.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class PickLocationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PickLocationUiState())
    val uiState: StateFlow<PickLocationUiState> = _uiState.asStateFlow()

    private val _event = Channel<PickLocationEvent>()
    val event: Flow<PickLocationEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>(
                key = "latitude",
                initialValue = null,
            ).combine(
                savedStateHandle.getStateFlow<String?>(
                    key = "longitude",
                    initialValue = null,
                )
            ) { latString, lngString ->
                val lat = latString?.toDoubleOrNull()
                    ?: return@combine null
                val lng = lngString?.toDoubleOrNull()
                    ?: return@combine null
                Location(latitude = lat, longitude = lng)
            }.collect { location -> _uiState.update { it.copy(location = location) } }
        }
    }

    fun onAction(action: PickLocationAction) {
        when (action) {
            is PickLocationAction.UpdateLocation -> {
                _uiState.update {
                    it.copy(location = action.location)
                }
            }

            is PickLocationAction.ConfirmSelection -> {
                viewModelScope.launch {
                    _event.send(PickLocationEvent.SelectionConfirmed)
                }
            }
        }
    }
}