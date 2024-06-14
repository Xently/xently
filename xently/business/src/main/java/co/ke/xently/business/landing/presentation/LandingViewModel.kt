package co.ke.xently.business.landing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.shops.data.source.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LandingViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val accessControlRepository: AccessControlRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shopRepository.findTop10ShopsOrderByIsActivated().collect {
                _uiState.update { state -> state.copy(shops = it) }
            }
        }
        viewModelScope.launch {
            accessControlRepository.findAccessControl().collect {
                _uiState.update { state -> state.copy(canAddShop = it.canAddShop) }
            }
        }
    }
}