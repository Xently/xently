package co.ke.xently.features.reviews.presentation.reviewrequest.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.error.toError
import co.ke.xently.features.reviews.presentation.components.UnderlinedHeadline
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestAction
import co.ke.xently.features.reviews.presentation.reviewrequest.ReviewRequestUiState
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.libraries.data.core.domain.error.AuthorisationError
import co.ke.xently.libraries.data.core.domain.error.RetryableError
import co.ke.xently.libraries.data.core.domain.error.UiTextError
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.toUiTextError

@Composable
internal fun ReviewRequestLazyColumn(
    state: ReviewRequestUiState,
    reviewCategories: LazyPagingItems<ReviewCategory>,
    modifier: Modifier = Modifier,
    onClickSubmit: () -> Unit,
    onAction: (ReviewRequestAction) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(
            key = "Rating brief",
            contentType = "Rating brief",
        ) {
            UnderlinedHeadline(
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Light,
                headline = stringResource(R.string.review_request_headline),
            )
        }
        when (val loadState = reviewCategories.loadState.refresh) {
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
                    ReviewRequestErrorContent(
                        error = error,
                        onClickRetry = reviewCategories::refresh,
                    )
                }
            }
        }

        when (val loadState = reviewCategories.loadState.prepend) {
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
                    ReviewRequestErrorContent(
                        error = error,
                        onClickRetry = reviewCategories::retry,
                    )
                }
            }
        }

        items(
            count = reviewCategories.itemCount,
            key = reviewCategories.itemKey { it.name },
            contentType = reviewCategories.itemContentType { "ReviewCategories" },
        ) { index ->
            val category = reviewCategories[index]!!
            ReviewCategoryCard(
                index = index,
                state = state,
                category = category,
                onAction = onAction,
            )
        }

        when (val loadState = reviewCategories.loadState.append) {
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
                    ReviewRequestErrorContent(
                        error = error,
                        onClickRetry = reviewCategories::retry,
                    )
                }
            }
        }

        item(key = "Submit button") {
            PrimaryButton(
                onClick = onClickSubmit,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.action_submit)
                    .toUpperCase(Locale.current),
            )
        }
    }
}

@Composable
private fun ReviewRequestErrorContent(error: UiTextError, onClickRetry: () -> Unit) {
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