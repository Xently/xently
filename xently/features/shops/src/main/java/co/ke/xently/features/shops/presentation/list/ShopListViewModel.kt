package co.ke.xently.features.shops.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopFilters
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.shops.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class ShopListViewModel @Inject constructor(
    private val repository: ShopRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShopListUiState())
    val uiState: StateFlow<ShopListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ShopListEvent>()
    val event: Flow<ShopListEvent> = _event.receiveAsFlow()

    private val _filters = MutableStateFlow(ShopFilters())

    val shops: Flow<PagingData<Shop>> = _filters.flatMapLatest { filters ->
        val url = repository.getShopsUrlAssociatedWithCurrentUser()
        repository.getShops(url = url, filters = filters)
    }

    fun onAction(action: ShopListAction) {
        when (action) {
            is ShopListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is ShopListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }

            is ShopListAction.DeleteShop -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.deleteShop(shop = action.shop)) {
                        is Result.Failure -> {
                            _event.send(
                                ShopListEvent.Error(
                                    result.error.asUiText(),
                                    result.error
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(ShopListEvent.Success(action))
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            is ShopListAction.SelectShop -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.selectShop(shop = action.shop)) {
                        is Result.Failure -> {
                            _event.send(
                                ShopListEvent.Error(
                                    result.error.asUiText(),
                                    result.error
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(ShopListEvent.Success(action))
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