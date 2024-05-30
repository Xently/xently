package com.kwanzatukule.features.customer.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.catalogue.presentation.components.ProductCard
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.home.R
import com.kwanzatukule.libraries.pagination.presentation.PaginatedLazyRow
import kotlinx.coroutines.flow.flow

@Composable
fun SuggestedProductListCard(
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier,
    onClick: (Product) -> Unit,
    addToOrRemoveFromShoppingCart: (Product) -> Unit,
) {
    val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
        Text(text = it.message!!)
        Button(onClick = products::retry) {
            Text(text = "Retry")
        }
    }
    Card(modifier = modifier) {
        TitledSection(title = stringResource(R.string.section_title_personal_suggestions)) {
            PaginatedLazyRow(
                items = products,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                emptyContentMessage = "No products",
                errorStateContent = errorStateContent,
                appendErrorStateContent = errorStateContent,
                prependErrorStateContent = errorStateContent,
            ) {
                items(products.itemCount, key = { products[it]!!.id }) { index ->
                    val product = products[index]!!
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.surface) {
                        ProductCard(
                            product = product,
                            onClick = { onClick(product) },
                            modifier = Modifier
                                .width(150.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = MaterialTheme.shapes.medium,
                                ),
                            addToOrRemoveFromShoppingCart = {
                                addToOrRemoveFromShoppingCart(
                                    product.copy(inShoppingCart = it)
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@XentlyPreview
@Composable
private fun SuggestedProductListCardPreview() {
    val suggestedProducts = listOf(
        Product(name = "Product Name", price = 100),
        Product(
            name = "Product with a very long name",
            price = 200,
            image = "https://example.com/product1.jpg",
        ),
        Product(
            name = "Product Name",
            price = 2_080,
            image = "https://example.com/product1.jpg",
        ),
        Product(
            name = "Product Name",
            price = 10_800,
            image = "https://example.com/product1.jpg",
        ),
        Product(
            name = "Product Name",
            price = 280,
            image = "https://example.com/product1.jpg",
        ),
        Product(
            name = "Product Name",
            price = 80,
            image = "https://example.com/product1.jpg",
        ),
    )
    val products = flow {
        emit(
            PagingData.from(
                suggestedProducts,
                LoadStates(
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                    LoadState.NotLoading(true),
                ),
            )
        )
    }.collectAsLazyPagingItems()
    KwanzaTukuleTheme {
        SuggestedProductListCard(
            products = products,
            onClick = {},
            addToOrRemoveFromShoppingCart = {},
        )
    }
}