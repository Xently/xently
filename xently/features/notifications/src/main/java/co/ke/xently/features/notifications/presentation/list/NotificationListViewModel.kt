package co.ke.xently.features.notifications.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.domain.NotificationFilters
import co.ke.xently.features.notifications.data.source.NotificationRepository
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
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class NotificationListViewModel @Inject constructor(
    private val repository: NotificationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationListUiState())
    val uiState: StateFlow<NotificationListUiState> = _uiState.asStateFlow()

    private val _event = Channel<NotificationListEvent>()
    val event: Flow<NotificationListEvent> = _event.receiveAsFlow()

    private val _filters = MutableStateFlow(NotificationFilters())

    val notifications: Flow<PagingData<Notification>> = _filters.flatMapLatest { filters ->
        Pager(
            PagingConfig(
                pageSize = 20,
            )
        ) {
            XentlyPagingSource { url ->
                repository.getNotifications(
                    url = url,
                    filters = filters,
                )
            }
        }.flow
    }.cachedIn(viewModelScope)

    fun onAction(action: NotificationListAction) {
        when (action) {
            is NotificationListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is NotificationListAction.Search -> {
                _filters.update { it.copy(query = action.query) }
            }
        }
    }
}