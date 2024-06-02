package com.kwanzatukule.features.customer.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.presentation.list.components.CustomerListItem
import com.kwanzatukule.features.route.presentation.components.RouteSummaryItem
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.customer.domain.error.DataError
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(component: CustomerListComponent, modifier: Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is CustomerListEvent.Error -> {
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

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(text = "Customers") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onClickCustomerEntry,
                text = { Text(text = "Add customer") },
                icon = {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add customer",
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            val customers = component.customers.collectAsLazyPagingItems()
            val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
                Text(text = it.message!!)
                Button(onClick = customers::retry) {
                    Text(text = "Retry")
                }
            }
            PaginatedLazyColumn(
                items = customers,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                emptyContentMessage = "No customers",
                errorStateContent = errorStateContent,
                appendErrorStateContent = errorStateContent,
                prependErrorStateContent = errorStateContent,
            ) {
                item("route-summary") {
                    RouteSummaryItem(route = state.route)
                }
                items(customers.itemCount, key = { customers[it]!!.id }) { index ->
                    val customer = customers[index]!!
                    CustomerListItem(
                        customer = customer,
                        onClick = { component.onClickCustomer(customer) },
                    ) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options for ${customer.name}",
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

private data class CustomerListContent(
    val uiState: CustomerListUiState,
    val customers: PagingData<Customer> = PagingData.empty(),
)

private class CustomerListContentParameterProvider : PreviewParameterProvider<CustomerListContent> {
    override val values: Sequence<CustomerListContent>
        get() {
            val route = Route(
                id = 1,
                name = "Kibera",
                description = "Kibera route description...",
                summary = RouteSummary(
                    bookedOrder = Random.nextInt(100),
                    variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    numberOfCustomers = Random.nextInt(100),
                    totalRouteCustomers = Random.nextInt(100),
                    geographicalDistance = Random.nextInt(1_000, 10_000),
                ),
            )
            val customers = List(10) {
                Customer(
                    name = "Kibera",
                    email = "customer.${it + 1}@example.com",
                    phone = "+2547123456${Random.nextInt(10, 99)}",
                    id = it.toLong() + 1,
                )
            }
            return sequenceOf(
                CustomerListContent(
                    uiState = CustomerListUiState(route = route),
                ),
                CustomerListContent(
                    uiState = CustomerListUiState(
                        route = route,
                        isLoading = true,
                    ),
                ),
                CustomerListContent(
                    uiState = CustomerListUiState(
                        route = route,
                        isLoading = true,
                    ),
                    customers = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                CustomerListContent(
                    uiState = CustomerListUiState(
                        route = route,
                        isLoading = true,
                    ),
                    customers = PagingData.from(
                        customers,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                CustomerListContent(
                    uiState = CustomerListUiState(
                        route = route,
                        isLoading = true,
                    ),
                    customers = PagingData.empty(
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
private fun CustomerListScreenPreview(
    @PreviewParameter(CustomerListContentParameterProvider::class)
    content: CustomerListContent,
) {
    KwanzaTukuleTheme {
        CustomerListScreen(
            component = CustomerListComponent.Fake(
                state = content.uiState,
                _customers = content.customers,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}