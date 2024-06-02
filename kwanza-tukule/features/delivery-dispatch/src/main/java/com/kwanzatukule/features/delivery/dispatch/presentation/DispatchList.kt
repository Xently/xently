package com.kwanzatukule.features.delivery.dispatch.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.domain.Driver
import com.kwanzatukule.features.delivery.dispatch.domain.error.DataError
import com.kwanzatukule.features.delivery.dispatch.presentation.components.DispatchCardItem
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.datetime.Clock

@Composable
fun DispatchList(component: DispatchListComponent, modifier: Modifier = Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is DispatchListEvent.Error -> {
                    val result = snackbarHostState.showSnackbar(
                        it.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                        actionLabel = if (it.type is DataError.Network) "Retry" else null,
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
    }
    Column(modifier = modifier) {
        val authenticationState = LocalAuthenticationState.current

        AnimatedVisibility(visible = state.isLoading || authenticationState.isSignOutInProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        val dispatches = component.dispatches.collectAsLazyPagingItems()
        val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
            Text(text = it.message!!)
            Button(onClick = dispatches::retry) {
                Text(text = "Retry")
            }
        }
        PaginatedLazyColumn(
            items = dispatches,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            emptyContentMessage = "No dispatches",
            errorStateContent = errorStateContent,
            appendErrorStateContent = errorStateContent,
            prependErrorStateContent = errorStateContent,
            contentPadding = PaddingValues(bottom = 16.dp, end = 16.dp, start = 16.dp),
        ) {
            items(dispatches.itemCount, key = { dispatches[it]!!.id }) { index ->
                val dispatch = dispatches[index]!!
                DispatchCardItem(
                    dispatch = dispatch,
                    onClickViewRoute = { component.onClickViewRoute(dispatch) },
                    onClickViewOrders = { component.onClickViewOrders(dispatch) },
                )
            }
        }
    }
}


private data class DispatchListContent(
    val uiState: DispatchListUiState,
    val dispatches: PagingData<Dispatch> = PagingData.empty(),
)

private class DispatchListContentParameterProvider : PreviewParameterProvider<DispatchListContent> {
    override val values: Sequence<DispatchListContent>
        get() {
            val dispatches = List(10) {
                Dispatch(
                    id = "ABCDEFGH${it + 1}",
                    date = Clock.System.now(),
                    driver = Driver(name = "John Doe"),
                    route = Route(
                        id = 1,
                        name = "Kibera",
                        description = "Kibera route description...",
                        summary = null,
                    ),
                    status = Dispatch.Status.entries.random(),
                )
            }
            return sequenceOf(
                DispatchListContent(
                    uiState = DispatchListUiState(),
                ),
                DispatchListContent(
                    uiState = DispatchListUiState(isLoading = true),
                ),
                DispatchListContent(
                    uiState = DispatchListUiState(isLoading = true),
                    dispatches = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                DispatchListContent(
                    uiState = DispatchListUiState(isLoading = true),
                    dispatches = PagingData.from(
                        dispatches,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                DispatchListContent(
                    uiState = DispatchListUiState(isLoading = true),
                    dispatches = PagingData.empty(
                        LoadStates(
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                        ),
                    ),
                ),
            )
        }
}

@XentlyPreview
@Composable
private fun DispatchListScreenPreview(
    @PreviewParameter(DispatchListContentParameterProvider::class)
    content: DispatchListContent,
) {
    KwanzaTukuleTheme {
        Surface {
            DispatchList(
                component = DispatchListComponent.Fake(
                    state = content.uiState,
                    _dispatches = content.dispatches,
                ),
            )
        }
    }
}
