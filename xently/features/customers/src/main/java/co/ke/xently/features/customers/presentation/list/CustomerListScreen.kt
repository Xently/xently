package co.ke.xently.features.customers.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.customers.R
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.data.domain.error.toCustomerError
import co.ke.xently.features.customers.presentation.list.components.CustomerListEmptyState
import co.ke.xently.features.customers.presentation.list.components.CustomerListLazyColumn
import co.ke.xently.features.customers.presentation.utils.asUiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

@Composable
fun CustomerListScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<CustomerListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val customers = viewModel.customers.collectAsLazyPagingItems()

    CustomerListScreen(
        state = state,
        event = event,
        customers = customers,
        modifier = modifier,
        topBar = topBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun CustomerListScreen(
    state: CustomerListUiState,
    event: CustomerListEvent?,
    customers: LazyPagingItems<Customer>,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null, is CustomerListEvent.Success -> Unit

            is CustomerListEvent.Error -> {
                snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                )
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
                    onSearch = { onAction(CustomerListAction.Search(it)) },
                    onQueryChange = { onAction(CustomerListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_customers_placeholder),
                )*/
            }
        },
    ) { paddingValues ->
        val refreshLoadState = customers.loadState.refresh
        val isRefreshing by remember(refreshLoadState, customers.itemCount) {
            derivedStateOf {
                refreshLoadState == LoadState.Loading
                        && customers.itemCount > 0
            }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = customers::refresh,
        ) {
            when {
                customers.itemCount == 0 && refreshLoadState is LoadState.NotLoading -> {
                    CustomerListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = stringResource(R.string.message_no_customers_found),
                        onClickRetry = customers::retry,
                    )
                }

                customers.itemCount == 0 && refreshLoadState is LoadState.Error -> {
                    val error = remember(refreshLoadState) {
                        runBlocking { refreshLoadState.error.toCustomerError() }
                    }
                    CustomerListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = error.asUiText().asString(),
                        canRetry = error is DataError.Network.Retryable,
                        onClickRetry = customers::retry,
                    )
                }

                customers.itemCount == 0 && refreshLoadState is LoadState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }

                else -> {
                    CustomerListLazyColumn(
                        customers = customers,
                        modifier = Modifier.matchParentSize(),
                        currentUserRanking = state.currentUserRanking,
                    )
                }
            }
        }
    }
}

private class CustomerListScreenUiState(
    val state: CustomerListUiState,
    val customers: PagingData<Customer> = PagingData.from(
        List(10) {
            Customer(
                name = "Customer $it",
                id = "${it + 1L}",
            )
        },
    ),
)

private class CustomerListUiStateParameterProvider :
    PreviewParameterProvider<CustomerListScreenUiState> {
    override val values: Sequence<CustomerListScreenUiState>
        get() = sequenceOf(
            CustomerListScreenUiState(state = CustomerListUiState()),
            CustomerListScreenUiState(state = CustomerListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun CustomerListScreenPreview(
    @PreviewParameter(CustomerListUiStateParameterProvider::class)
    state: CustomerListScreenUiState,
) {
    val customers = flowOf(state.customers).collectAsLazyPagingItems()
    XentlyTheme {
        CustomerListScreen(
            state = state.state,
            event = null,
            customers = customers,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
