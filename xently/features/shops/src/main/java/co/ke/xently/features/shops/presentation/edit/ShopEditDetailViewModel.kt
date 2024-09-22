package co.ke.xently.features.shops.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.merchant.data.domain.MerchantDataValidator
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopDataValidator
import co.ke.xently.features.shops.data.source.ShopRepository
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
import co.ke.xently.features.merchant.data.domain.error.Result as MerchantResult
import co.ke.xently.features.shops.data.domain.error.Result as ShopResult

@HiltViewModel
internal class ShopEditDetailViewModel @Inject constructor(
    private val repository: ShopRepository,
    private val shopDataValidator: ShopDataValidator,
    private val merchantDataValidator: MerchantDataValidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopEditDetailUiState())
    val uiState: StateFlow<ShopEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ShopEditDetailEvent>()
    val event: Flow<ShopEditDetailEvent> = _event.receiveAsFlow()


    fun onAction(action: ShopEditDetailAction) {
        when (action) {
            is ShopEditDetailAction.ClearFieldsForNewShop -> {
                onAction(ShopEditDetailAction.ChangeShopName(""))
                onAction(ShopEditDetailAction.ChangeShopWebsite(""))
                _uiState.update {
                    it.copy(
                        nameError = null,
                        websiteError = null,
                    )
                }
            }

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

            ShopEditDetailAction.ClickSave, ShopEditDetailAction.ClickSaveAndAddAnother -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(
                            isLoading = true,
                            nameError = null,
                            websiteError = null,
                            merchantFirstNameError = null,
                            merchantLastNameError = null,
                            merchantEmailAddressError = null,
                        )
                    }
                    val shop = validatedShop(state)
                    val merchant = validatedMerchant(state)

                    if (_uiState.value.isFormValid) {
                        when (val result = repository.save(shop = shop, merchant = merchant)) {
                            is ShopResult.Failure -> {
                                _event.send(
                                    ShopEditDetailEvent.Error(
                                        result.error.toUiText(),
                                        result.error,
                                    )
                                )
                            }

                            is ShopResult.Success -> {
                                _event.send(ShopEditDetailEvent.Success(action))
                            }
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

    private fun validatedShop(state: ShopEditDetailUiState): Shop {
        var shop = state.shop

        when (val result = shopDataValidator.validatedWebsite(state.website)) {
            is ShopResult.Failure -> _uiState.update { it.copy(websiteError = result.error) }
            is ShopResult.Success -> {
                _uiState.update { it.copy(websiteError = null) }
                shop = shop.copy(onlineShopUrl = result.data)
            }
        }

        when (val result = shopDataValidator.validatedName(state.name)) {
            is ShopResult.Failure -> _uiState.update { it.copy(nameError = result.error) }
            is ShopResult.Success -> {
                _uiState.update { it.copy(nameError = null) }
                shop = shop.copy(name = result.data)
            }
        }

        return shop
    }

    private fun validatedMerchant(state: ShopEditDetailUiState): Merchant {
        var merchant = state.merchant

        when (val result = merchantDataValidator.validatedEmail(state.merchantEmailAddress)) {
            is MerchantResult.Failure -> _uiState.update { it.copy(merchantEmailAddressError = result.error) }
            is MerchantResult.Success -> {
                _uiState.update { it.copy(merchantEmailAddressError = null) }
                merchant = merchant.copy(emailAddress = result.data)
            }
        }

        when (val result = merchantDataValidator.validatedName(state.merchantFirstName)) {
            is MerchantResult.Failure -> _uiState.update { it.copy(merchantFirstNameError = result.error) }
            is MerchantResult.Success -> {
                _uiState.update { it.copy(merchantFirstNameError = null) }
                merchant = merchant.copy(firstName = result.data)
            }
        }

        when (val result = merchantDataValidator.validatedName(state.merchantLastName)) {
            is MerchantResult.Failure -> _uiState.update { it.copy(merchantLastNameError = result.error) }
            is MerchantResult.Success -> {
                _uiState.update { it.copy(merchantLastNameError = null) }
                merchant = merchant.copy(lastName = result.data)
            }
        }

        return merchant
    }
}