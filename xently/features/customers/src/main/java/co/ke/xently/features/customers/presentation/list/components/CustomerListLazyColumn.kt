package co.ke.xently.features.customers.presentation.list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import co.ke.xently.features.customers.R
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.toError
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.ScrollToTheTopEffectIfNecessary
import co.ke.xently.libraries.data.core.AuthorisationError
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.data.core.UiTextError
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.toUiTextError

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CustomerListLazyColumn(
    currentUserRanking: Customer?,
    customers: LazyPagingItems<Customer>,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyListState()

    ScrollToTheTopEffectIfNecessary(state = state)

    LazyColumn(
        state = state,
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
                    // Ignore loading state for refresh...
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Refresh Error",
                    contentType = "Refresh Error",
                ) {
                    val error = loadState.error.toUiTextError { it.toError() } ?: return@item
                    CustomerListErrorContent(
                        error = error,
                        onClickRetry = customers::refresh,
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
                    val error = loadState.error.toUiTextError { it.toError() } ?: return@item
                    CustomerListErrorContent(
                        error = error,
                        onClickRetry = customers::retry,
                    )
                }
            }
        }
        if (currentUserRanking != null) {
            stickyHeader(key = "current-user-ranking", contentType = "current-user-ranking") {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        fontWeight = FontWeight.Light,
                        text = stringResource(
                            R.string.you_are_position_with_points,
                            currentUserRanking.position,
                            currentUserRanking.totalPoints,
                        ),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 16.dp),
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            stickyHeader(key = "total-customers", contentType = "total-customers") {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = pluralStringResource(
                            R.plurals.customers_total_title,
                            customers.itemCount,
                            customers.itemCount,
                        ),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 16.dp),
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
                    val error = loadState.error.toUiTextError { it.toError() } ?: return@item
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
private fun CustomerListErrorContent(error: UiTextError, onClickRetry: () -> Unit) {
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