package com.kwanzatukule.features.catalogue.presentation.productlist


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.error.DataError
import com.kwanzatukule.features.catalogue.presentation.components.ProductCard
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.libraries.pagination.presentation.PaginatedLazyVerticalGrid


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    modifier: Modifier,
    component: ProductListComponent,
    shoppingCartBadge: @Composable () -> Unit,
) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is ProductListEvent.Error -> {
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
                title = { Text(text = state.category?.name ?: "Catalogue") },
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
                        Icon(Icons.Default.Search, contentDescription = "Search catalogue...")
                    }
                    shoppingCartBadge()
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
            val products = component.products.collectAsLazyPagingItems()
            val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
                Text(text = it.message!!)
                Button(onClick = products::retry) {
                    Text(text = "Retry")
                }
            }
            PaginatedLazyVerticalGrid(
                items = products,
                modifier = Modifier.weight(1f),
                columns = GridCells.Adaptive(150.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                emptyContentMessage = "No products",
                errorStateContent = errorStateContent,
                appendErrorStateContent = errorStateContent,
                prependErrorStateContent = errorStateContent,
            ) {
                items(products.itemCount, key = { products[it]!!.id }) { index ->
                    val product = products[index]!!
                    ProductCard(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .padding(PaddingValues(horizontal = 16.dp)),
                        product = product,
                        onClick = { component.navigateToProductDetail(product) },
                        addToOrRemoveFromShoppingCart = {
                            component.addToOrRemoveFromShoppingCart(
                                product.copy(inShoppingCart = it)
                            )
                        },
                    )
                }
            }
        }
    }
}

private class ProductListUiStateParameterProvider : PreviewParameterProvider<ProductListUiState> {
    override val values: Sequence<ProductListUiState>
        get() {
            val category = Category(
                name = "Category 1",
                description = "Category 1 description",
            )
            return sequenceOf(
                ProductListUiState(),
                ProductListUiState(isLoading = true),
                ProductListUiState(category = category),
                ProductListUiState(category = category, isLoading = true),
            )
        }
}

@KwanzaPreview
@Composable
private fun ProductListScreenPreview(
    @PreviewParameter(ProductListUiStateParameterProvider::class)
    uiState: ProductListUiState,
) {
    KwanzaTukuleTheme {
        ProductListScreen(
            component = ProductListComponent.Fake(uiState),
            modifier = Modifier.fillMaxSize(),
            shoppingCartBadge = {},
        )
    }
}