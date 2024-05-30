package com.kwanzatukule.features.order.presentation.list

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
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.presentation.list.components.OrderCardItem
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import com.kwanzatukule.libraries.pagination.presentation.PaginatedLazyColumn
import kotlin.random.Random

@Composable
fun OrderList(component: OrderListComponent, modifier: Modifier = Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is OrderListEvent.Error -> {
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

                is OrderListEvent.FindNavigableLocations -> Unit
            }
        }
    }
    Column(modifier = modifier) {
        val authenticationState = LocalAuthenticationState.current

        AnimatedVisibility(visible = state.isLoading || authenticationState.isSignOutInProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        val orders = component.orders.collectAsLazyPagingItems()
        val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
            Text(text = it.message!!)
            Button(onClick = orders::retry) {
                Text(text = "Retry")
            }
        }
        PaginatedLazyColumn(
            items = orders,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            emptyContentMessage = "No orders",
            errorStateContent = errorStateContent,
            appendErrorStateContent = errorStateContent,
            prependErrorStateContent = errorStateContent,
            contentPadding = PaddingValues(16.dp),
        ) {
            items(orders.itemCount, key = { orders[it]!!.id }) { index ->
                val order = orders[index]!!
                OrderCardItem(
                    order = order,
                    onClickViewShoppingList = { component.onClickViewShoppingList(order) },
                )
            }
        }
    }
}


private data class OrderListContent(
    val uiState: OrderListUiState,
    val orders: PagingData<Order> = PagingData.empty(),
)

private class OrderListContentParameterProvider : PreviewParameterProvider<OrderListContent> {
    override val values: Sequence<OrderListContent>
        get() {
            val orders = List(10) {
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
                val customer = Customer(
                    id = 1,
                    name = "John Doe",
                    email = "customer@example.com",
                    phone = "+2547123456${Random.nextInt(10, 99)}",
                )
                Order(
                    id = "ORDER${it + 1}",
                    customer = customer,
                    route = route,
                    status = Order.Status.entries.random(),
                )
            }
            return sequenceOf(
                OrderListContent(
                    uiState = OrderListUiState(),
                ),
                OrderListContent(
                    uiState = OrderListUiState(isLoading = true),
                ),
                OrderListContent(
                    uiState = OrderListUiState(isLoading = true),
                    orders = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                OrderListContent(
                    uiState = OrderListUiState(isLoading = true),
                    orders = PagingData.from(
                        orders,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                OrderListContent(
                    uiState = OrderListUiState(isLoading = true),
                    orders = PagingData.empty(
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
private fun OrderListScreenPreview(
    @PreviewParameter(OrderListContentParameterProvider::class)
    content: OrderListContent,
) {
    KwanzaTukuleTheme {
        Surface {
            OrderList(
                component = OrderListComponent.Fake(
                    state = content.uiState,
                    _orders = content.orders,
                ),
            )
        }
    }
}
