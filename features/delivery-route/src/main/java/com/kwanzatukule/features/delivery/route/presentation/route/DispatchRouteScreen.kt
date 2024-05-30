package com.kwanzatukule.features.delivery.route.presentation.route

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.pages.Pages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.delivery.route.domain.Page
import com.kwanzatukule.features.delivery.route.presentation.map.OrderMap
import com.kwanzatukule.features.delivery.route.presentation.route.DispatchRouteNavigationComponent.Child
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.presentation.list.OrderList
import com.kwanzatukule.features.order.presentation.list.OrderListComponent
import com.kwanzatukule.features.order.presentation.list.OrderListUiState
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlin.random.Random

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalDecomposeApi::class,
)
@Composable
fun DispatchRouteScreen(
    component: DispatchRouteNavigationComponent,
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
) {
    val childPages by component.childPages.subscribeAsState()
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Dispatch route") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        },
        bottomBar = bottomBar,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            PrimaryTabRow(selectedTabIndex = childPages.selectedIndex) {
                Page.entries.forEach { page ->
                    Tab(
                        selected = childPages.selectedIndex == page.ordinal,
                        onClick = { component.selectPage(page.ordinal) },
                        text = {
                            Text(
                                text = stringResource(page.title),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        /*icon = {
                            Icon(
                                page.icon,
                                contentDescription = stringResource(page.title),
                            )
                        },*/
                    )
                }
            }
            Pages(
                modifier = Modifier.weight(1f),
                pages = component.childPages,
                onPageSelected = component::selectPage,
                scrollAnimation = PagesScrollAnimation.Default,
            ) { _, page ->
                when (page) {
                    is Child.OrderList -> {
                        when (page.page) {
                            Page.List -> OrderList(
                                component = page.component,
                                modifier = Modifier.fillMaxSize(),
                            )

                            Page.Map -> OrderMap(
                                component = page.component,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }
        }
    }
}


private data class DispatchRouteContent(
    val uiState: OrderListUiState,
    val orders: PagingData<Order> = PagingData.empty(),
)

private class DispatchRouteContentParameterProvider :
    PreviewParameterProvider<DispatchRouteContent> {
    override val values: Sequence<DispatchRouteContent>
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
                DispatchRouteContent(
                    uiState = OrderListUiState(),
                ),
                DispatchRouteContent(
                    uiState = OrderListUiState(isLoading = true),
                ),
                DispatchRouteContent(
                    uiState = OrderListUiState(isLoading = true),
                    orders = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                DispatchRouteContent(
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
                DispatchRouteContent(
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
private fun DispatchRouteScreenPreview(
    @PreviewParameter(DispatchRouteContentParameterProvider::class)
    content: DispatchRouteContent,
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