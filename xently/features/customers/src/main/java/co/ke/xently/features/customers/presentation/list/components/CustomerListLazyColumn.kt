package co.ke.xently.features.customers.presentation.list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import co.ke.xently.features.customers.R
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.Error
import co.ke.xently.features.customers.data.domain.error.toCustomerError
import co.ke.xently.features.customers.presentation.utils.asUiText
import kotlinx.coroutines.runBlocking
import co.ke.xently.features.customers.data.domain.error.DataError as CustomerDataError

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CustomerListLazyColumn(
    currentUserRanking: Customer?,
    customers: LazyPagingItems<Customer>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (val loadState = customers.loadState.refresh) {
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
                        runBlocking { loadState.error.toCustomerError() }
                    }
                    CustomerListErrorContent(
                        error = error,
                        onClickRetry = customers::retry,
                    )
                }
            }
        }

        when (val loadState = customers.loadState.prepend) {
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
                        runBlocking { loadState.error.toCustomerError() }
                    }
                    CustomerListErrorContent(
                        error = error,
                        onClickRetry = customers::retry,
                    )
                }
            }
        }
        currentUserRanking?.let { ranking ->
            stickyHeader(key = "current-user-ranking", contentType = "current-user-ranking") {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = stringResource(
                            R.string.you_are_position_with_points,
                            ranking.position,
                            ranking.totalPoints,
                        ),
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }
            }
        }

        items(
            count = customers.itemCount,
            key = customers.itemKey { it.id },
            contentType = customers.itemContentType { "Customers" },
        ) { index ->
            val customer = customers[index]

            if (customer != null) {
                CustomerListItem(
                    customer = customer,
                    position = index + 1,
                )
            }
        }

        when (val loadState = customers.loadState.append) {
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
                        runBlocking { loadState.error.toCustomerError() }
                    }
                    CustomerListErrorContent(
                        error = error,
                        onClickRetry = customers::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerListErrorContent(error: Error, onClickRetry: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = error.asUiText().asString(),
            modifier = Modifier.weight(1f),
        )
        if (error is CustomerDataError.Network.Retryable) {
            Button(onClick = onClickRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}