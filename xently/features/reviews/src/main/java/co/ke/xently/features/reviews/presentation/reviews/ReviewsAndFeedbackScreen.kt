package co.ke.xently.features.reviews.presentation.reviews

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.presentation.components.GeneralReviewSummary
import co.ke.xently.features.reviews.presentation.components.ReviewCategoryListSection
import co.ke.xently.features.reviews.presentation.components.ReviewContent
import co.ke.xently.features.reviews.presentation.theme.STAR_RATING_COLOURS
import co.ke.xently.features.reviews.presentation.utils.UiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import com.aay.compose.barChart.model.BarParameters
import kotlinx.datetime.Month
import kotlin.random.Random
import co.ke.xently.features.reviewcategory.data.domain.error.DataError as ReviewCategoryDataError

@Composable
fun ReviewsAndFeedbackScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
) {
    val viewModel = hiltViewModel<ReviewsAndFeedbackViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)

    ReviewsAndFeedbackScreen(
        state = state,
        event = event,
        modifier = modifier,
        onClickBack = onClickBack,
        onAction = viewModel::onAction,
        onClickViewComments = onClickViewComments,
        onClickAddNewReviewCategory = onClickAddNewReviewCategory,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewsAndFeedbackScreen(
    state: ReviewsAndFeedbackUiState,
    event: ReviewsAndFeedbackEvent?,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onAction: (ReviewsAndFeedbackAction) -> Unit,
    onClickViewComments: (ReviewCategory) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            ReviewsAndFeedbackEvent.Success -> onClickBack()
            is ReviewsAndFeedbackEvent.Error.ReviewCategories -> {
                val result = snackbarHostState.showSnackbar(
                    event.error.asString(context = context),
                    duration = SnackbarDuration.Long,
                    actionLabel = if (event.type is ReviewCategoryDataError.Network) {
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

            is ReviewsAndFeedbackEvent.Error.ReviewsAndFeedback -> {
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
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.topbar_title_reviews),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.basicMarquee(),
                    )
                },
                navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                onClickSelectCategory = { onAction(ReviewsAndFeedbackAction.SelectReviewCategory(it)) },
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
                        onClickRetry = { onAction(ReviewsAndFeedbackAction.FetchStoreStatistics) },
                        onClickViewComments = { onClickViewComments(selectedCategory) },
                        onClickApplyFilters = { onAction(ReviewsAndFeedbackAction.FetchStoreStatistics) },
                        onClickSelectYear = { onAction(ReviewsAndFeedbackAction.SelectYear(it)) },
                        onClickSelectMonth = { onAction(ReviewsAndFeedbackAction.SelectMonth(it)) },
                        onClickRemoveMonth = {
                            onAction(ReviewsAndFeedbackAction.RemoveSelectedMonth(it))
                        },
                        onClickRemoveYear = {
                            onAction(ReviewsAndFeedbackAction.RemoveSelectedYear(it))
                        },
                    )
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
                        type = DataError.Network.UNKNOWN,
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
            event = null,
            onClickBack = {},
            onClickAddNewReviewCategory = {},
            onAction = {},
            onClickViewComments = {},
        )
    }
}