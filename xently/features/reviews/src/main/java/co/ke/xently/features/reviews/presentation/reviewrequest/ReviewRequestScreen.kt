package co.ke.xently.features.reviews.presentation.reviewrequest

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.toError
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.presentation.reviewrequest.components.ReviewRequestEmptyState
import co.ke.xently.features.reviews.presentation.reviewrequest.components.ReviewRequestLazyColumn
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.libraries.data.core.AuthorisationError
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import co.ke.xently.libraries.ui.pagination.ListState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import co.ke.xently.libraries.ui.pagination.asListState

@Composable
fun ReviewRequestScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<ReviewRequestViewModel>()
    val reviewCategories = viewModel.reviewCategories.collectAsLazyPagingItems()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                ReviewRequestEvent.Success -> Unit
                is ReviewRequestEvent.Error -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }

    ReviewRequestScreen(
        state = state,
        modifier = modifier,
        reviewCategories = reviewCategories,
        snackbarHostState = snackbarHostState,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewRequestScreen(
    state: ReviewRequestUiState,
    reviewCategories: LazyPagingItems<ReviewCategory>,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    onClickBack: () -> Unit,
    onAction: (ReviewRequestAction) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                title = { Text(text = stringResource(R.string.topbar_title_review_request)) },
            )
        },
    ) { paddingValues ->
        val refreshLoadState = reviewCategories.loadState.refresh
        val isRefreshing by remember(refreshLoadState, reviewCategories.itemCount) {
            derivedStateOf {
                refreshLoadState == LoadState.Loading
                        && reviewCategories.itemCount > 0
            }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = reviewCategories::refresh,
        ) {
            when (val listState =
                refreshLoadState.asListState(reviewCategories.itemCount, Throwable::toError)) {
                ListState.Empty -> {
                    ReviewRequestEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = stringResource(R.string.message_no_review_categories_found),
                        onClickRetry = reviewCategories::refresh,
                    )
                }

                ListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }

                ListState.NotLoading -> {
                    ReviewRequestLazyColumn(
                        state = state,
                        onAction = onAction,
                        reviewCategories = reviewCategories,
                        modifier = Modifier.matchParentSize(),
                        onClickSubmit = onClickBack,
                    )
                }

                is ListState.Error -> {
                    ReviewRequestEmptyState(
                        modifier = Modifier.matchParentSize(),
                        message = listState.error.asString(),
                        canRetry = listState.error is RetryableError,
                        onClickRetry = reviewCategories::retry,
                    ) {
                        if (listState.error is AuthorisationError) {
                            Spacer(modifier = Modifier.height(16.dp))

                            LoginAndRetryButtonsRow(onRetry = reviewCategories::retry)
                        }
                    }
                }
            }
        }
    }
}