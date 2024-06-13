package co.ke.xently.features.shops.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.shops.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ShopEditDetailViewModel @Inject constructor(
    private val repository: ShopRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopEditDetailUiState())
    val uiState: StateFlow<ShopEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ShopEditDetailEvent>()
    val event: Flow<ShopEditDetailEvent> = _event.receiveAsFlow()


    fun onAction(action: ShopEditDetailAction) {
        when (action) {
            is ShopEditDetailAction.ChangeMerchantFirstName -> {
                _uiState.update {
                    it.copy(merchantFirstName = action.merchantFirstName)
                }
            }

            is ShopEditDetailAction.ChangeMerchantLastName -> {
                _uiState.update {
                    it.copy(merchantLastName = action.merchantLastName)
                }
            }

            is ShopEditDetailAction.ChangeMerchantEmailAddress -> {
                _uiState.update {
                    it.copy(merchantEmailAddress = action.merchantEmailAddress)
                }
            }

            is ShopEditDetailAction.ChangeShopWebsite -> {
                _uiState.update {
                    it.copy(website = action.website)
                }
            }

            is ShopEditDetailAction.ChangeShopName -> {
                _uiState.update {
                    it.copy(name = action.name)
                }
            }

            ShopEditDetailAction.ClickSaveDetails -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    val shop = state.shop.copy(
                        name = state.name,
                        onlineShopUrl = state.website.takeIf { it.isNotBlank() },
                    )
                    when (val result = repository.save(shop = shop)) {
                        is Result.Failure -> {
                            _event.send(
                                ShopEditDetailEvent.Error(
                                    result.error.asUiText(),
                                    result.error,
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(ShopEditDetailEvent.Success)
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}