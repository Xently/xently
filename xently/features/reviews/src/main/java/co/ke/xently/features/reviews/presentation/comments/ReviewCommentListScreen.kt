package co.ke.xently.features.reviews.presentation.comments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.presentation.comments.components.ReviewCommentListItem
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.pagination.PaginatedLazyColumn
import kotlinx.coroutines.flow.flowOf
import kotlin.random.Random

@Composable
fun ReviewCommentListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<ReviewCommentListViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val reviews = viewModel.reviews.collectAsLazyPagingItems()

    ReviewCommentListScreen(
        state = state,
        event = event,
        reviews = reviews,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewCommentListScreen(
    state: ReviewCommentListUiState,
    event: ReviewCommentListEvent?,
    reviews: LazyPagingItems<Review>,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onAction: (ReviewCommentListAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is ReviewCommentListEvent.Error -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is DataError.Network) {
                        context.getString(R.string.action_retry)
                    } else {
                        null
                    },
                )

                when (result) {
                    SnackbarResult.Dismissed -> {

                    }

                    SnackbarResult.ActionPerformed -> {

                    }
                }
            }

            is ReviewCommentListEvent.Success -> {

            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                TopAppBar(
                    windowInsets = WindowInsets.waterfall,
                    title = { Text(text = stringResource(R.string.top_bar_title_review_comments)) },
                    navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                )
                AnimatedVisibility(state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                /*SearchBar(
                    query = state.query,
                    onSearch = { onAction(ReviewCommentListAction.Search(it)) },
                    onQueryChange = { onAction(ReviewCommentListAction.ChangeQuery(it)) },
                    placeholder = stringResource(R.string.search_reviews_placeholder),
                )*/

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        state.stars,
                        key = { it.number },
                    ) { star ->
                        FilterChip(
                            selected = star.selected,
                            onClick = {
                                onAction(ReviewCommentListAction.SelectStarRating(star))
                            },
                            label = {
                                Text(
                                    text = stringResource(
                                        R.string.action_label_numbered_star,
                                        star
                                    )
                                )
                            },
                            trailingIcon = if (!star.selected) null else {
                                {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(
                                            R.string.content_desc_unselect_numbered_star,
                                            star,
                                        ),
                                        modifier = Modifier
                                            .size(InputChipDefaults.AvatarSize)
                                            .clickable {
                                                onAction(
                                                    ReviewCommentListAction.RemoveStarRating(
                                                        star
                                                    )
                                                )
                                            },
                                    )
                                }
                            },
                        )

                    }
                }
            }
        },
    ) { paddingValues ->
        PaginatedLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            items = reviews,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            emptyContentMessage = "No reviews found",
            prependErrorStateContent = {},
            appendErrorStateContent = {},
            errorStateContent = {},
            contentPadding = PaddingValues(16.dp),
        ) {
            items(
                reviews.itemCount,
                key = {
                    reviews[it]?.links?.get("self")
                        ?.hrefWithoutQueryParamTemplates()
                        ?: (reviews.itemCount + it).toString()
                },
            ) {
                val review = reviews[it]!!

                ReviewCommentListItem(review = review)
            }
        }
    }
}

private class ReviewCommentListScreenUiState(
    val state: ReviewCommentListUiState,
    val reviews: PagingData<Review> = PagingData.from(
        List(10) {
            Review(
                starRating = Random.nextInt(1, 6),
                message = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                reviewerName = "John Doe",
                links = mapOf(
                    "self" to Link("https://jsonplaceholder.typicode.com/posts/1")
                ),
            )
        },
    ),
)

private class ReviewCommentListScreenUiStateParameterProvider :
    PreviewParameterProvider<ReviewCommentListScreenUiState> {
    override val values: Sequence<ReviewCommentListScreenUiState>
        get() = sequenceOf(
            ReviewCommentListScreenUiState(state = ReviewCommentListUiState()),
            ReviewCommentListScreenUiState(state = ReviewCommentListUiState(isLoading = true)),
        )
}

@XentlyPreview
@Composable
private fun ProductListScreenPreview(
    @PreviewParameter(ReviewCommentListScreenUiStateParameterProvider::class)
    state: ReviewCommentListScreenUiState,
) {
    val reviews = flowOf(state.reviews).collectAsLazyPagingItems()
    XentlyTheme {
        ReviewCommentListScreen(
            state = state.state,
            event = null,
            reviews = reviews,
            onClickBack = {},
            onAction = {},
        )
    }
}
