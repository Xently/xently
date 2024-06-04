package co.ke.xently.features.stores.presentation.locationpickup

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

const val SELECTED_STORE_LOCATION_KEY =
    "co.ke.xently.features.stores.presentation.locationpickup.SELECTED_STORE_LOCATION"

@HiltViewModel
internal class PickStoreLocationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PickStoreLocationUiState())
    val uiState: StateFlow<PickStoreLocationUiState> = _uiState.asStateFlow()

    fun onAction(action: PickStoreLocationAction) {
        when (action) {
            is PickStoreLocationAction.UpdateLocation -> {
                _uiState.update {
                    it.copy(location = action.location)
                }
            }

            is PickStoreLocationAction.ConfirmSelection -> {
                savedStateHandle[SELECTED_STORE_LOCATION_KEY] = action
            }
        }

    }
}