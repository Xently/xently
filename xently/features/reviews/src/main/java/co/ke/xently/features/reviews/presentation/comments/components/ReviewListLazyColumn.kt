package co.ke.xently.features.reviews.presentation.comments.components

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.error.Error
import co.ke.xently.features.reviews.data.domain.error.toError
import co.ke.xently.features.reviews.presentation.utils.asUiText
import kotlinx.coroutines.runBlocking
import co.ke.xently.features.reviews.data.domain.error.DataError as ReviewDataError

@Composable
internal fun ReviewListLazyColumn(
    reviews: LazyPagingItems<Review>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        when (val loadState = reviews.loadState.refresh) {
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
                        runBlocking { loadState.error.toError() }
                    }
                    ReviewListErrorContent(
                        error = error,
                        onClickRetry = reviews::retry,
                    )
                }
            }
        }

        when (val loadState = reviews.loadState.prepend) {
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
                        runBlocking { loadState.error.toError() }
                    }
                    ReviewListErrorContent(
                        error = error,
                        onClickRetry = reviews::retry,
                    )
                }
            }
        }

        items(
            count = reviews.itemCount,
            key = reviews.itemKey {
                it.links["self"]!!.hrefWithoutQueryParamTemplates()
            },
            contentType = reviews.itemContentType { "Reviews" },
        ) { index ->
            val review = reviews[index]

            if (review != null) {
                ReviewCommentListItem(review = review)
            } else {
                ReviewCommentListItem(review = Review.DEFAULT, isLoading = true)
            }
        }

        when (val loadState = reviews.loadState.append) {
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
                        runBlocking { loadState.error.toError() }
                    }
                    ReviewListErrorContent(
                        error = error,
                        onClickRetry = reviews::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewListErrorContent(error: Error, onClickRetry: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = error.asUiText().asString(),
            modifier = Modifier.weight(1f),
        )
        if (error is ReviewDataError.Network.Retryable) {
            Button(onClick = onClickRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}