package co.ke.xently.features.notifications.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.notifications.R
import co.ke.xently.features.notifications.data.domain.Notification
import co.ke.xently.features.notifications.data.domain.error.DataError
import co.ke.xently.features.notifications.presentation.list.components.NotificationListItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

@Composable
fun NotificationListScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<NotificationListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val notifications = viewModel.notifications.collectAsLazyPagingItems()

    NotificationListScreen(
        state = state,
        event = event,
        notifications = notifications,
        modifier = modifier,
        topBar = topBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationListScreen(
    state: NotificationListUiState,
    event: NotificationListEvent?,
    notifications: LazyPagingItems<Notification>,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null, is NotificationListEvent.Success -> Unit

            is NotificationListEvent.Error -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is DataError.Network) {
                        context.getString(R.string.action_retry)
                    } else {
                        null
                    },
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {

                    }
                }
            }
        }
    }

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
        PaginatedLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            items = notifications,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            emptyContentMessage = "No notifications found",
            prependErrorStateContent = {},
            appendErrorStateContent = {},
            errorStateContent = {},
        ) {
            items(
                notifications.itemCount,
                key = {
                    notifications[it]?.id
                        ?: ">>>${(notifications.itemCount + it)}<<<"
                },
            ) {
                val notification = notifications[it]

                if (notification != null) {
                    NotificationListItem(notification = notification)
                } else {
                    NotificationListItem(
                        isLoading = true,
                        notification = Notification.DEFAULT,
                    )
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
            event = null,
            notifications = notifications,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
