package com.kwanzatukule.features.order.presentation.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.cart.presentation.LocalShoppingCartState
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartTotalBottomBarCard
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.presentation.list.components.CustomerListItem
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.presentation.components.ShoppingCartLineCart
import com.kwanzatukule.features.route.presentation.list.components.RouteListItem
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(component: OrderSummaryComponent, modifier: Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is OrderSummaryEvent.Error -> {
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
    val shoppingCart by LocalShoppingCartState.current

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Checkout") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                },
            )
        },
        bottomBar = {
            ShoppingCartTotalBottomBarCard(
                shoppingCart = shoppingCart,
                submitLabel = "Place Order",
                onClickSubmit = component::onClickPlaceOrder,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = "Customer",
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                        },
                        trailingContent = if (!LocalCanUpdateOrderSummaryCustomer.current) null else {
                            {
                                IconButton(onClick = component::onClickUpdateCustomer) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Update customer",
                                    )
                                }
                            }
                        },
                    )
                    CustomerListItem(customer = state.order.customer)
                    HorizontalDivider()
                }
                item {
                    ListItem(
                        headlineContent = {
                            Text(
                                text = "Shipping address",
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = component::onClickUpdateRoute) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Update route",
                                )
                            }
                        },
                    )
                    RouteListItem(route = state.order.route)
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {
                            Text(
                                text = "Shopping cart",
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = component::onClickUpdateShoppingCart) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Update shopping cart",
                                )
                            }
                        },
                    )
                }
                items(shoppingCart.items, key = { it.id }) { item ->
                    ShoppingCartLineCart(item = item)
                    HorizontalDivider()
                }
            }
        }
    }
}

private class OrderSummaryUiStateParameterProvider :
    PreviewParameterProvider<OrderSummaryUiState> {
    override val values: Sequence<OrderSummaryUiState>
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
            val customer = Customer(
                id = 1,
                name = "John Doe Groceries",
                email = "customer@example.com",
                phone = "+2547123456${Random.nextInt(10, 99)}",
            )
            val order = Order(customer = customer, route = route)
            return sequenceOf(
                OrderSummaryUiState(order = order),
                OrderSummaryUiState(
                    order = order,
                    isLoading = true,
                ),
            )
        }
}

@KwanzaPreview
@Composable
private fun OrderSummaryScreenPreview(
    @PreviewParameter(OrderSummaryUiStateParameterProvider::class)
    uiState: OrderSummaryUiState,
) {
    val shoppingCart = ShoppingCart(
        items = listOf(
            ShoppingCart.Item(
                Product(
                    name = "Random product name",
                    price = 1256,
                    image = "https://picsum.photos/200/300",
                ),
                1,
            ),
            ShoppingCart.Item(
                Product(
                    name = "Random product name",
                    price = 456,
                    image = "https://picsum.photos/200/300",
                ),
                3,
            ),
            ShoppingCart.Item(
                Product(
                    name = "Random product name",
                    price = 234,
                    image = "https://picsum.photos/200/300",
                ),
                1,
            ),
        ).mapIndexed { index, item -> item.copy(id = (index + 1).toLong()) },
    )
    KwanzaTukuleTheme {
        val shoppingCartMutableState = remember { mutableStateOf(shoppingCart) }
        CompositionLocalProvider(LocalShoppingCartState provides shoppingCartMutableState) {
            OrderSummaryScreen(
                component = OrderSummaryComponent.Fake(uiState),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}