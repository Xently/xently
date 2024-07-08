package co.ke.xently.features.customers.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.customers.R
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.ConfigurationError
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.data.domain.error.UnknownError
import co.ke.xently.features.customers.data.domain.error.toError
import co.ke.xently.features.customers.presentation.list.components.CustomerListEmptyState
import co.ke.xently.features.customers.presentation.list.components.CustomerListLazyColumn
import co.ke.xently.features.customers.presentation.utils.asUiText
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.SearchBar
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.random.Random

@Composable
fun CustomerListScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<CustomerListViewModel>()

    CustomerScoreboardListScreen(
        viewModel = viewModel,
        modifier = modifier,
        topBar = topBar,
    )
}

@Composable
fun CustomerScoreboardListScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
) {
    val viewModel = hiltViewModel<CustomerScoreboardListViewModel>()

    CustomerScoreboardListScreen(
        viewModel = viewModel,
        modifier = modifier,
        topBar = topBar,
    )
}

@Composable
private fun CustomerScoreboardListScreen(
    viewModel: CustomerScoreboardListViewModel,
    modifier: Modifier,
    topBar: @Composable () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val customers = viewModel.customers.collectAsLazyPagingItems()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is CustomerListEvent.Success -> Unit

                is CustomerListEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    CustomerListScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        customers = customers,
        modifier = modifier,
        topBar = topBar,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CustomerListScreen(
    state: CustomerListUiState,
    snackbarHostState: SnackbarHostState,
    customers: LazyPagingItems<Customer>,
    modifier: Modifier = Modifier,
    onAction: (CustomerListAction) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val eventHandler = LocalEventHandler.current

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                topBar()
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                SearchBar(
                    query = state.query,
                    onSearch = { onAction(CustomerListAction.Search(it)) },
                    onQueryChange = { onAction(CustomerListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_customers_placeholder),
                )
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
                        onClickRetry = customers::refresh,
                    )
                }

                customers.itemCount == 0 && refreshLoadState is LoadState.Error -> {
                    val error = remember(refreshLoadState) {
                        runBlocking { refreshLoadState.error.toError() }
                    }
                    CustomerListEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = error.asUiText().asString(),
                        canRetry = error is DataError.Network.Retryable || error is UnknownError,
                        onClickRetry = customers::retry,
                    ) {
                        when (error) {
                            ConfigurationError.ShopSelectionRequired -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = eventHandler::requestShopSelection) {
                                    Text(text = stringResource(R.string.action_select_shop))
                                }
                            }

                            ConfigurationError.StoreSelectionRequired -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = eventHandler::requestStoreSelection) {
                                    Text(text = stringResource(R.string.action_select_store))
                                }
                            }

                            DataError.Network.Unauthorized -> {
                                Spacer(modifier = Modifier.height(16.dp))

                                LoginAndRetryButtonsRow(onRetry = customers::retry)
                            }

                            else -> Unit
                        }
                    }
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
            CustomerListScreenUiState(
                state = CustomerListUiState(
                    currentUserRanking = Customer(
                        id = UUID.randomUUID().toString(),
                        visitCount = Random.nextInt(1, 50),
                        totalPoints = Random.nextInt(101, 1000),
                        position = Random.nextInt(1, 10),
                        placesVisitedCount = Random.nextInt(1, 10),
                        links = mapOf(
                            "self" to Link(href = "https://jsonplaceholder.typicode.com/posts/${UUID.randomUUID()}")
                        ),
                    ),
                ),
            ),
            CustomerListScreenUiState(
                state = CustomerListUiState(
                    isLoading = true,
                    currentUserRanking = Customer(
                        id = UUID.randomUUID().toString(),
                        visitCount = Random.nextInt(1, 50),
                        totalPoints = Random.nextInt(101, 1000),
                        position = Random.nextInt(1, 10),
                        placesVisitedCount = Random.nextInt(1, 10),
                        links = mapOf(
                            "self" to Link(href = "https://jsonplaceholder.typicode.com/posts/${UUID.randomUUID()}")
                        ),
                    ),
                ),
            ),
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
            snackbarHostState = rememberSnackbarHostState(),
            customers = customers,
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
