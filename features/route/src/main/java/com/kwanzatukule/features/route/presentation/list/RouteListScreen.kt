package com.kwanzatukule.features.route.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
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
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.route.presentation.list.components.RouteListItem
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import com.kwanzatukule.libraries.data.route.domain.error.DataError
import com.kwanzatukule.libraries.pagination.presentation.PaginatedLazyColumn
import kotlin.random.Random

@Composable
fun RouteListScreen(
    component: RouteListComponent,
    modifier: Modifier,
    contentWindowInsets: WindowInsets,
    topBar: @Composable () -> Unit,
) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is RouteListEvent.Error -> {
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
        topBar = topBar,
        contentWindowInsets = contentWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            if (LocalCanAddRoute.current) {
                ExtendedFloatingActionButton(
                    onClick = component::onClickRouteEntry,
                    text = { Text("Add route") },
                    icon = { Icon(Icons.Default.AddRoad, contentDescription = "Add route") },
                )
            }
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
            val routes = component.routes.collectAsLazyPagingItems()
            val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
                Text(text = it.message!!)
                Button(onClick = routes::retry) {
                    Text(text = "Retry")
                }
            }
            PaginatedLazyColumn(
                items = routes,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                emptyContentMessage = "No routes",
                errorStateContent = errorStateContent,
                appendErrorStateContent = errorStateContent,
                prependErrorStateContent = errorStateContent,
            ) {
                items(routes.itemCount, key = { routes[it]!!.id }) { index ->
                    val route = routes[index]!!
                    RouteListItem(route = route, onClick = { component.onClickRoute(route) }) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "More options for ${route.name}",
                            )
                        }
                    }
                }
            }
        }
    }
}


private data class RouteListContent(
    val uiState: RouteListUiState,
    val routes: PagingData<Route> = PagingData.empty(),
)

private class RouteListContentParameterProvider : PreviewParameterProvider<RouteListContent> {
    override val values: Sequence<RouteListContent>
        get() {
            val routes = List(10) {
                Route(
                    name = "Kibera",
                    description = "Kibera route description...",
                    id = it.toLong() + 1,
                    summary = RouteSummary(
                        bookedOrder = Random.nextInt(100),
                        variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                        numberOfCustomers = Random.nextInt(100),
                        totalRouteCustomers = Random.nextInt(100),
                        geographicalDistance = Random.nextInt(1_000, 10_000),
                    ),
                )
            }
            return sequenceOf(
                RouteListContent(
                    uiState = RouteListUiState(),
                ),
                RouteListContent(
                    uiState = RouteListUiState(isLoading = true),
                ),
                RouteListContent(
                    uiState = RouteListUiState(isLoading = true),
                    routes = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                RouteListContent(
                    uiState = RouteListUiState(isLoading = true),
                    routes = PagingData.from(
                        routes,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                RouteListContent(
                    uiState = RouteListUiState(isLoading = true),
                    routes = PagingData.empty(
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

@OptIn(ExperimentalMaterial3Api::class)
@XentlyPreview
@Composable
private fun RouteListScreenPreview(
    @PreviewParameter(RouteListContentParameterProvider::class)
    content: RouteListContent,
) {
    KwanzaTukuleTheme {
        RouteListScreen(
            component = RouteListComponent.Fake(
                state = content.uiState,
                _routes = content.routes,
            ),
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
            topBar = { TopAppBar(title = { Text(text = "Routes") }) },
        )
    }
}
