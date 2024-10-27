package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import co.ke.xently.features.products.data.domain.error.toError
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.ScrollToTheTopEffectIfNecessary
import co.ke.xently.libraries.data.core.domain.error.AuthorisationError
import co.ke.xently.libraries.data.core.domain.error.RetryableError
import co.ke.xently.libraries.data.core.domain.error.UiTextError
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.toUiTextError

@Composable
internal fun ProductListLazyColumn(
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier,
    extraPrependContent: LazyListScope.() -> Unit,
    productListItem: @Composable LazyItemScope.(Product?) -> Unit,
) {
    val state = rememberLazyListState()

    ScrollToTheTopEffectIfNecessary(state = state)

    LazyColumn(
        state = state,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (val loadState = products.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                items(count = 20) {
                    productListItem(null)
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Refresh Error",
                    contentType = "Refresh Error",
                ) {
                    val error = loadState.error.toUiTextError { it.toError() } ?: return@item
                    ProductListErrorContent(
                        error = error,
                        onClickRetry = products::refresh,
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
                    val error = loadState.error.toUiTextError { it.toError() } ?: return@item
                    ProductListErrorContent(
                        error = error,
                        onClickRetry = products::retry,
                    )
                }
            }
        }

        extraPrependContent()

        items(
            count = products.itemCount,
            key = products.itemKey { it.id },
            contentType = products.itemContentType { "Products" },
        ) { index ->
            val product = products[index]

            productListItem(product)
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
                    val error = loadState.error.toUiTextError { it.toError() } ?: return@item
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
private fun ProductListErrorContent(error: UiTextError, onClickRetry: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = error.asString(),
            modifier = Modifier.weight(1f),
        )
        if (error is RetryableError) {
            Button(onClick = onClickRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        } else if (error is AuthorisationError) {
            val eventHandler = LocalEventHandler.current
            val authenticationState by LocalAuthenticationState.current

            if (authenticationState.isAuthenticated) {
                LaunchedEffect(Unit) {
                    onClickRetry()
                }
            } else {
                LaunchedEffect(Unit) {
                    eventHandler.requestAuthentication()
                }
            }

            Button(onClick = eventHandler::requestAuthentication) {
                Text(text = stringResource(R.string.action_login))
            }
        }
    }
}