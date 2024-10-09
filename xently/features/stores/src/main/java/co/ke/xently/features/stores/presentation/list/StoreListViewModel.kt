package co.ke.xently.features.stores.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import timber.log.Timber
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class StoreListViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val webSocketClient: StompWebSocketClient,
) : ViewModel() {
    companion object {
        private val TAG = StoreListViewModel::class.java.simpleName
    }

    private val _uiState = MutableStateFlow(StoreListUiState())
    val uiState: StateFlow<StoreListUiState> = _uiState.asStateFlow()

    private val _event = Channel<StoreListEvent>()
    val event: Flow<StoreListEvent> = _event.receiveAsFlow()

    private val _filters = MutableStateFlow(StoreFilters())

    val stores = _filters.flatMapLatest { filters ->
        val url = repository.getDefaultStoreFetchUrl()
        repository.getStores(filters = filters, url = url)
    }.cachedIn(viewModelScope)

    val searchSuggestions = webSocketClient.watch {
        val destination = "/user/queue/type-ahead.stores"
//        val destination = "/queue/type-ahead.stores"
        Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
        subscribe<List<String>>(destination = destination)
    }.catch {
        Timber.tag(TAG).e(it, "An unexpected error was encountered.")
    }

    private var typeAheadJob: Job? = null

    fun onAction(action: StoreListAction) {
        when (action) {
            is StoreListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
                typeAheadJob?.cancel()
                typeAheadJob = viewModelScope.launch {
                    webSocketClient.sendMessage {
                        convertAndSend(
                            destination = "/app/type-ahead.stores",
                            body = co.ke.xently.libraries.data.core.TypeAheadSearchRequest(query = action.query),
                        )
                    }
                }
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