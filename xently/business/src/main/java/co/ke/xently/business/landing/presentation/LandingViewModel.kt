package co.ke.xently.business.landing.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.features.auth.data.source.UserRepository
import co.ke.xently.features.auth.presentation.utils.asUiText
import co.ke.xently.features.shops.data.source.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LandingViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository,
    private val accessControlRepository: AccessControlRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LandingUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<LandingEvent>()
    val event: Flow<LandingEvent> = _event.receiveAsFlow()

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
        viewModelScope.launch {
            userRepository.getCurrentUser().collect {
                _uiState.update { state -> state.copy(user = it) }
            }
        }
    }

    fun onAction(action: LandingAction) {
        when (action) {
            LandingAction.ClickSignOut -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isSignOutInProgress = true)
                    }
                    when (val result = signOut()) {
                        is Result.Failure -> {
                            _event.send(LandingEvent.Error(result.error.asUiText(), result.error))
                        }

                        is Result.Success -> _event.send(LandingEvent.Success)
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isSignOutInProgress = false)
                    }
                }
            }
        }
    }

    private suspend fun signOut(): Result<Unit, DataError> {
        return userRepository.signOut()
    }
}