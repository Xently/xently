package co.ke.xently.business.landing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shopRepository.findTop10ShopsOrderByIsActivated().collect { shops ->
                _uiState.update { it.copy(shops = shops) }
            }
        }
    }
}