package co.ke.xently.features.stores.presentation.list

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.source.StoreRepository
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
        val url = repository.getDefaultStoreFetchUrl()
        repository.getStores(filters = filters, url = url)
    }

    fun onAction(action: StoreListAction) {
        when (action) {
            is StoreListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is StoreListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }

            is StoreListAction.ToggleBookmark -> {
//                TODO()
            }
        }
    }
}