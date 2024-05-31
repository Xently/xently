package com.kwanzatukule.features.cart.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
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
import co.ke.xently.libraries.pagination.presentation.PaginatedLazyColumn
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.cart.domain.error.DataError
import com.kwanzatukule.features.cart.presentation.components.ShoppingListItem
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(component: ShoppingListComponent, modifier: Modifier = Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is ShoppingListEvent.Error -> {
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
            CenterAlignedTopAppBar(
                title = { Text(text = "Shopping list") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            val authenticationState = LocalAuthenticationState.current

            AnimatedVisibility(visible = state.isLoading || authenticationState.isSignOutInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            val shoppingList = component.shoppingList.collectAsLazyPagingItems()
            val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
                Text(text = it.message!!)
                Button(onClick = shoppingList::retry) {
                    Text(text = "Retry")
                }
            }
            PaginatedLazyColumn(
                items = shoppingList,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                emptyContentMessage = "No shopping list",
                errorStateContent = errorStateContent,
                appendErrorStateContent = errorStateContent,
                prependErrorStateContent = errorStateContent,
            ) {
                items(shoppingList.itemCount, key = { shoppingList[it]!!.id }) { index ->
                    val shoppingListItem = shoppingList[index]!!
                    ShoppingListItem(item = shoppingListItem)
                }
            }
        }
    }
}


private data class ShoppingListContent(
    val uiState: ShoppingListUiState,
    val shoppingList: PagingData<ShoppingCart.Item> = PagingData.empty(),
)

private class ShoppingListContentParameterProvider : PreviewParameterProvider<ShoppingListContent> {
    override val values: Sequence<ShoppingListContent>
        get() {
            val shoppingList = List(10) {
                ShoppingCart.Item(
                    quantity = 3,
                    product = Product(
                        name = "Bananas",
                        price = 456,
                        image = "https://picsum.photos/200/300",
                    ),
                )
            }
            return sequenceOf(
                ShoppingListContent(
                    uiState = ShoppingListUiState(),
                ),
                ShoppingListContent(
                    uiState = ShoppingListUiState(isLoading = true),
                ),
                ShoppingListContent(
                    uiState = ShoppingListUiState(isLoading = true),
                    shoppingList = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                ShoppingListContent(
                    uiState = ShoppingListUiState(isLoading = true),
                    shoppingList = PagingData.from(
                        shoppingList,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                ShoppingListContent(
                    uiState = ShoppingListUiState(isLoading = true),
                    shoppingList = PagingData.empty(
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
private fun ShoppingListScreenPreview(
    @PreviewParameter(ShoppingListContentParameterProvider::class)
    content: ShoppingListContent,
) {
    KwanzaTukuleTheme {
        Surface {
            ShoppingListScreen(
                component = ShoppingListComponent.Fake(
                    state = content.uiState,
                    _shoppingList = content.shoppingList,
                ),
            )
        }
    }
}
