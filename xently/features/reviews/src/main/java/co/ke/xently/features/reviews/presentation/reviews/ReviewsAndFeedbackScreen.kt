package co.ke.xently.features.reviews.presentation.reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.UnknownError
import co.ke.xently.features.reviews.presentation.components.GeneralReviewSummary
import co.ke.xently.features.reviews.presentation.components.ReviewCategoryListSection
import co.ke.xently.features.reviews.presentation.components.ReviewContent
import co.ke.xently.features.reviews.presentation.theme.STAR_RATING_COLOURS
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.rememberSnackbarHostState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import com.aay.compose.barChart.model.BarParameters
import kotlinx.datetime.Month
import kotlin.random.Random

@Composable
fun ReviewsAndFeedbackScreen(
    modifier: Modifier = Modifier,
    onClickAddNewReviewCategory: () -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val viewModel = hiltViewModel<ReviewsAndFeedbackViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = rememberSnackbarHostState()

    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                ReviewsAndFeedbackEvent.Success -> Unit
                is ReviewsAndFeedbackEvent.Error.ReviewCategories -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }

                is ReviewsAndFeedbackEvent.Error.ReviewsAndFeedback -> {
                    snackbarHostState.showSnackbar(
                        event.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                    )
                }
            }
        }
    }
    ReviewsAndFeedbackScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
        onClickAddNewReviewCategory = onClickAddNewReviewCategory,
        onAction = viewModel::onAction,
        onClickViewComments = onClickViewComments,
        topBar = topBar,
    )
}

@Composable
internal fun ReviewsAndFeedbackScreen(
    state: ReviewsAndFeedbackUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onClickAddNewReviewCategory: () -> Unit,
    onAction: (ReviewsAndFeedbackAction) -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val context = LocalContext.current
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        val isRefreshing by remember(state) {
            derivedStateOf {
                state.statisticsResponse is StatisticsResponse.Loading
                        && state.categoriesResponse is ReviewCategoriesResponse.Loading
                        && state.shopReviewSummaryResponse is ReviewSummaryResponse.Loading
                        && state.storeReviewSummaryResponse is ReviewSummaryResponse.Loading
            }
        }
        PullRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            isRefreshing = isRefreshing,
            onRefresh = { onAction(ReviewsAndFeedbackAction.Refresh(context)) },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                GeneralReviewSummary(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.section_title_shop_review_summary),
                    response = state.shopReviewSummaryResponse,
                    onClickRetry = { onAction(ReviewsAndFeedbackAction.FetchShopReviewSummary) },
                )

                GeneralReviewSummary(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.section_title_store_review_summary),
                    response = state.storeReviewSummaryResponse,
                    onClickRetry = { onAction(ReviewsAndFeedbackAction.FetchStoreReviewSummary) },
                )

                ReviewCategoryListSection(
                    response = state.categoriesResponse,
                    selectedCategory = state.selectedCategory,
                    onClickRetry = { onAction(ReviewsAndFeedbackAction.FetchReviewCategories) },
                    onClickSelectCategory = { category ->
                        onAction(
                            ReviewsAndFeedbackAction.SelectReviewCategory(
                                context = context,
                                category = category,
                            )
                        )
                    },
                    onClickMoreCategoryOptions = { /*TODO*/ },
                    onClickAddNewReviewCategory = onClickAddNewReviewCategory,
                )

                state.statisticsResponse?.let { response ->
                    state.selectedCategory?.let { selectedCategory ->
                        ReviewContent(
                            modifier = Modifier,
                            category = selectedCategory,
                            filters = state.selectedFilters,
                            response = response,
                            onClickRetry = {
                                onAction(ReviewsAndFeedbackAction.FetchStoreStatistics(context))
                            },
                            onClickViewComments = { onClickViewComments(selectedCategory) },
                            onClickApplyFilters = {
                                onAction(ReviewsAndFeedbackAction.FetchStoreStatistics(context))
                            },
                            onClickSelectYear = { onAction(ReviewsAndFeedbackAction.SelectYear(it)) },
                            onClickSelectMonth = { onAction(ReviewsAndFeedbackAction.SelectMonth(it)) },
                            onClickRemoveMonth = {
                                onAction(ReviewsAndFeedbackAction.RemoveSelectedMonth)
                            },
                            onClickRemoveYear = {
                                onAction(ReviewsAndFeedbackAction.RemoveSelectedYear)
                            },
                        )
                    }
                }
            }
        }
    }
}

private class ReviewsUiStateProvider : PreviewParameterProvider<ReviewsAndFeedbackUiState> {
    override val values: Sequence<ReviewsAndFeedbackUiState>
        get() {
            val shopReviewSummaryResponse = ReviewSummaryResponse.Success(
                Rating(
                    average = 3.5f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, Random.nextLong(10_000, 1_000_000))
                    }.sortedByDescending { it.star },
                )
            )
            val storeReviewSummaryResponse = ReviewSummaryResponse.Success(
                Rating(
                    average = 4.5f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, Random.nextLong(10_000, 100_000))
                    }.sortedByDescending { it.star },
                )
            )
            val selectedFilters = ReviewStatisticsFilters(
                year = 2022,
                month = Month.entries.random(),
            )
            val selectedCategory = ReviewCategory(name = "Staff friendliness")
            val categoriesResponse = ReviewCategoriesResponse.Success.NonEmpty(
                listOf(
                    selectedCategory,
                    ReviewCategory(name = "Ambience"),
                    ReviewCategory(name = "Cleanliness"),
                ),
            )
            return sequenceOf(
                ReviewsAndFeedbackUiState(
                    shopReviewSummaryResponse = shopReviewSummaryResponse,
                    storeReviewSummaryResponse = storeReviewSummaryResponse,
                    categoriesResponse = categoriesResponse,
                    selectedCategory = selectedCategory,
                    statisticsResponse = StatisticsResponse.Success(
                        data = ReviewCategory.Statistics(
                            totalReviews = 100,
                            generalSentiment = ReviewCategory.Statistics.GeneralSentiment.entries.random(),
                            averageRating = 3.7f,
                            percentageSatisfaction = Random.nextInt(0, 100),
                            groupedStatistics = List(10) {
                                ReviewCategory.Statistics.GroupedStatistic(
                                    group = "Group $it",
                                    starRating = Random.nextInt(1, 5),
                                    count = 100,
                                )
                            },
                        ),
                        barGraphData = BarGraphData(
                            xAxis = listOf("Jan", "Feb", "Mar"),
                            barParameters = List(5) {
                                BarParameters(
                                    dataName = "${it + 1}-star",
                                    data = listOf(10.0, 20.0, 30.0),
                                    barColor = STAR_RATING_COLOURS[it + 1]!!,
                                )
                            },
                        ),
                    ),
                    selectedFilters = selectedFilters,
                ),
                ReviewsAndFeedbackUiState(
                    shopReviewSummaryResponse = shopReviewSummaryResponse,
                    storeReviewSummaryResponse = storeReviewSummaryResponse,
                    categoriesResponse = categoriesResponse,
                    selectedCategory = selectedCategory,
                    statisticsResponse = StatisticsResponse.Loading,
                    selectedFilters = selectedFilters,
                ),
                ReviewsAndFeedbackUiState(
                    shopReviewSummaryResponse = shopReviewSummaryResponse,
                    storeReviewSummaryResponse = storeReviewSummaryResponse,
                    categoriesResponse = categoriesResponse,
                    selectedCategory = selectedCategory,
                    statisticsResponse = StatisticsResponse.Failure(
                        error = UiText.DynamicString("Example error message"),
                        type = UnknownError,
                    ),
                    selectedFilters = selectedFilters,
                ),
                ReviewsAndFeedbackUiState(
                    shopReviewSummaryResponse = shopReviewSummaryResponse,
                    storeReviewSummaryResponse = storeReviewSummaryResponse,
                    categoriesResponse = categoriesResponse,
                    selectedCategory = selectedCategory,
                    selectedFilters = selectedFilters,
                ),
            )
        }
}

@XentlyPreview
@Composable
private fun ReviewsScreenPreview(
    @PreviewParameter(ReviewsUiStateProvider::class)
    state: ReviewsAndFeedbackUiState,
) {
    XentlyTheme {
        ReviewsAndFeedbackScreen(
            state = state,
            snackbarHostState = rememberSnackbarHostState(),
            onClickAddNewReviewCategory = {},
            onAction = {},
            onClickViewComments = {},
        )
    }
}