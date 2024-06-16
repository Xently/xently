package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.Error
import co.ke.xently.features.products.data.domain.error.toProductError
import co.ke.xently.features.products.presentation.utils.asUiText
import kotlinx.coroutines.runBlocking
import co.ke.xently.features.products.data.domain.error.DataError as ProductDataError

@Composable
internal fun ProductListLazyColumn(
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier,
    onClickEditProduct: (Product) -> Unit,
    onClickConfirmDelete: (Product) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (val loadState = products.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item(
                    key = "Refresh Loading",
                    contentType = "Refresh Loading",
                ) {
                    Text(
                        text = "Waiting for items to load from the backend",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Refresh Error",
                    contentType = "Refresh Error",
                ) {
                    val error = remember(loadState.error) {
                        runBlocking { loadState.error.toProductError() }
                    }
                    ProductListErrorContent(
                        error = error,
                        onClickRetry = products::retry,
                    )
                }
            }
        }

        when (val loadState = products.loadState.prepend) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item(
                    key = "Prepend Loading",
                    contentType = "Prepend Loading",
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Prepend Error",
                    contentType = "Prepend Error",
                ) {
                    val error = remember(loadState.error) {
                        runBlocking { loadState.error.toProductError() }
                    }
                    ProductListErrorContent(
                        error = error,
                        onClickRetry = products::retry,
                    )
                }
            }
        }

        items(
            count = products.itemCount,
            key = products.itemKey { it.id },
            contentType = products.itemContentType { "Products" },
        ) { index ->
            val product = products[index]

            if (product != null) {
                ProductListItem(
                    product = product,
                    onClickUpdate = { onClickEditProduct(product) },
                    onClickConfirmDelete = { onClickConfirmDelete(product) },
                )
            } else {
                ProductListItem(
                    product = Product.DEFAULT,
                    isLoading = true,
                    onClickUpdate = {},
                    onClickConfirmDelete = {},
                )
            }
        }

        when (val loadState = products.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item(
                    key = "Append Loading",
                    contentType = "Append Loading",
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Append Error",
                    contentType = "Append Error",
                ) {
                    val error = remember(loadState.error) {
                        runBlocking { loadState.error.toProductError() }
                    }
                    ProductListErrorContent(
                        error = error,
                        onClickRetry = products::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductListErrorContent(error: Error, onClickRetry: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = error.asUiText().asString(),
            modifier = Modifier.weight(1f),
        )
        if (error is ProductDataError.Network.Retryable) {
            Button(onClick = onClickRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}