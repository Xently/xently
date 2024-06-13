package co.ke.xently.features.stores.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.features.stores.presentation.utils.asUiText
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
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
internal class StoreListViewModel @Inject constructor(
    private val repository: StoreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(StoreListUiState())
    val uiState: StateFlow<StoreListUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreListEvent>()
    val event: Flow<StoreListEvent> = _event.receiveAsFlow()

    private val _filters = MutableStateFlow(StoreFilters())

    val stores: Flow<PagingData<Store>> = _filters.flatMapLatest { filters ->
        Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
            )
        ) {
            XentlyPagingSource { url ->
                repository.getStores(
                    url = url,
                    filters = filters,
                )
            }
        }.flow
    }.cachedIn(viewModelScope)

    fun onAction(action: StoreListAction) {
        when (action) {
            is StoreListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is StoreListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }

            is StoreListAction.DeleteStore -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.deleteStore(store = action.store)) {
                        is Result.Failure -> {
                            _event.send(
                                StoreListEvent.Error(
                                    result.error.asUiText(),
                                    result.error
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(StoreListEvent.Success(action))
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            is StoreListAction.SelectStore -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.selectStore(store = action.store)) {
                        is Result.Failure -> {
                            _event.send(
                                StoreListEvent.Error(
                                    result.error.asUiText(),
                                    result.error
                                )
                            )
                        }

                        is Result.Success -> {
                            _event.send(StoreListEvent.Success(action))
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