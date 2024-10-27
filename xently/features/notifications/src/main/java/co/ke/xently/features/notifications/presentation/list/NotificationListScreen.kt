package co.ke.xently.features.notifications.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.notifications.R
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.domain.error.DataError
import co.ke.xently.features.notifications.data.domain.error.toError
import co.ke.xently.features.notifications.presentation.list.components.NotificationListEmptyState
import co.ke.xently.features.notifications.presentation.list.components.NotificationListLazyColumn
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.domain.error.RetryableError
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import co.ke.xently.libraries.ui.pagination.ListState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import co.ke.xently.libraries.ui.pagination.asListState
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

@Composable
fun NotificationListScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<NotificationListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is NotificationListEvent.Success -> Unit

                is NotificationListEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    NotificationListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        notifications = notifications,
        modifier = modifier,
        topBar = topBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationListScreen(
    state: NotificationListUiState,
    snackbarHostState: SnackbarHostState,
    notifications: LazyPagingItems<Notification>,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                topBar()
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(NotificationListAction.Search(it)) },
                    onQueryChange = { onAction(NotificationListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_notifications_placeholder),
                )*/
            }
        },
    ) { paddingValues ->
        val refreshLoadState = notifications.loadState.refresh
        val isRefreshing by remember(refreshLoadState, notifications.itemCount) {
            derivedStateOf {
                refreshLoadState == LoadState.Loading
                        && notifications.itemCount > 0
            }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = notifications::refresh,
        ) {
            when (val listState =
                refreshLoadState.asListState(notifications.itemCount, Throwable::toError)) {
                ListState.Empty -> {
                    NotificationListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = stringResource(R.string.message_no_notifications_found),
                        onClickRetry = notifications::refresh,
                    )
                }

                ListState.Loading, ListState.Ready -> {
                    NotificationListLazyColumn(
                        notifications = notifications,
                        modifier = Modifier.matchParentSize(),
                    )
                }

                is ListState.Error -> {
                    NotificationListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = listState.error.asString(),
                        canRetry = listState.error is RetryableError,
                        onClickRetry = notifications::retry,
                    ) {
                        when (listState.error) {
                            DataError.Network.Unauthorized -> {
                                Spacer(modifier = Modifier.height(16.dp))

                                LoginAndRetryButtonsRow(onRetry = notifications::retry)
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}

private class NotificationListScreenUiState(
    val state: NotificationListUiState,
    val notifications: PagingData<Notification> = PagingData.from(
        List(10) {
            Notification(
                id = 1L + it,
                timeSent = Clock.System.now(),
                message = Notification.Message(
                    title = "Notification title",
                    message = "New deal 50% off on all meals at the new Imara Daima Hotel",
                ),
            )
        },
    ),
)

private class NotificationListUiStateParameterProvider :
    PreviewParameterProvider<NotificationListScreenUiState> {
    override val values: Sequence<NotificationListScreenUiState>
        get() = sequenceOf(
            NotificationListScreenUiState(state = NotificationListUiState()),
            NotificationListScreenUiState(state = NotificationListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun NotificationListScreenPreview(
    @PreviewParameter(NotificationListUiStateParameterProvider::class)
    state: NotificationListScreenUiState,
) {
    val notifications = flowOf(state.notifications).collectAsLazyPagingItems()
    XentlyTheme {
        NotificationListScreen(
            state = state.state,
            snackbarHostState = rememberSnackbarHostState(),
            notifications = notifications,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
