package co.ke.xently.features.customers.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.customers.R
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.presentation.list.components.CustomerListItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import kotlinx.coroutines.flow.flowOf

@Composable
fun CustomerListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
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
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun CustomerListScreen(
    state: CustomerListUiState,
    event: CustomerListEvent?,
    customers: LazyPagingItems<Customer>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (CustomerListAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null, is CustomerListEvent.Success -> Unit

            is CustomerListEvent.Error -> {
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
                CenterAlignedTopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_customer_list)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
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
        PaginatedLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            items = customers,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            emptyContentMessage = "No customers found",
            prependErrorStateContent = {},
            appendErrorStateContent = {},
            errorStateContent = {},
        ) {
            state.currentUserRanking?.let { ranking ->
                stickyHeader(key = "current-user-ranking") {
                    Surface(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            fontWeight = FontWeight.Bold,
                            text = "You are position ${ranking.position} with ${ranking.totalPoints} points",
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }
                }
            }

            items(
                customers.itemCount,
                key = {
                    customers[it]?.id
                        ?: (customers.itemCount + it).toLong()
                },
            ) {
                val customer = customers[it]!!

                CustomerListItem(
                    customer = customer,
                    position = it + 1,
                )
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
            onClickBack = {},
            onAction = {},
        )
    }
}
