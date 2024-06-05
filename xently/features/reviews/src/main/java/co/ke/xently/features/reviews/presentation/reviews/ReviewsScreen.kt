package co.ke.xently.features.reviews.presentation.reviews

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
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
import co.ke.xently.features.reviewcategory.presentation.components.ReviewCategoryItem
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.presentation.components.GeneralReviewSummary
import co.ke.xently.features.reviews.presentation.components.ReviewContent
import co.ke.xently.features.reviews.presentation.components.UnderlinedHeadline
import co.ke.xently.features.reviews.presentation.theme.STAR_RATING_COLOURS
import co.ke.xently.features.reviews.presentation.utils.UiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import com.aay.compose.barChart.model.BarParameters
import kotlinx.datetime.Month
import kotlin.random.Random

@Composable
fun ReviewsScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
) {
    val viewModel = hiltViewModel<ReviewsViewModel>()

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    val statisticsResponse by viewModel.statisticsResponse.collectAsStateWithLifecycle(null)

    ReviewsScreen(
        state = state,
        event = event,
        statisticsResponse = statisticsResponse,
        modifier = modifier,
        onClickBack = onClickBack,
        onClickAddNewReviewCategory = onClickAddNewReviewCategory,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewsScreen(
    state: ReviewsUiState,
    event: ReviewsEvent?,
    statisticsResponse: StatisticsResponse?,
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onAction: (ReviewsAction) -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            is ReviewsEvent.Error -> {
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

            ReviewsEvent.Success -> onClickBack()
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
            state.shopRating?.let { rating ->
                GeneralReviewSummary(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.section_title_shop_review_summary),
                    rating = rating,
                )
            }

            state.storeRating?.let { rating ->
                GeneralReviewSummary(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    headline = stringResource(R.string.section_title_store_review_summary),
                    rating = rating,
                )
            }

            UnderlinedHeadline(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                headline = stringResource(R.string.section_title_review_category),
                trailingContent = {
                    Surface(
                        content = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.action_add_review_category),
                            )
                        },
                        onClick = onClickAddNewReviewCategory,
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                    )
                },
            )

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                for (category in state.categories) {
                    ReviewCategoryItem(
                        modifier = Modifier,
                        category = category,
                        selected = state.selectedCategory?.name == category.name,
                        onClick = {
                            onAction(ReviewsAction.SelectReviewCategory(category))
                        },
                        onClickMoreOptions = {
//                            onAction(ReviewsAction.SelectReviewCategory(category))
                        },
                    )
                }
            }

            state.statisticsResponse?.let { response ->
                state.selectedCategory?.let { selectedCategory ->
                    ReviewContent(
                        modifier = Modifier.padding(16.dp),
                        category = selectedCategory,
                        filters = state.selectedFilters,
                        statisticsResponse = response,
                        onClickRefreshStatistics = {},
                        onClickViewComments = {},
                        onClickApplyFilters = {},
                        onClickSelectYear = {},
                        onClickSelectMonth = {},
                    )
                }
            }
        }
    }
}

private class ReviewsUiStateProvider : PreviewParameterProvider<ReviewsUiState> {
    override val values: Sequence<ReviewsUiState>
        get() {
            val shopRating = Rating(
                average = 3.5f,
                totalPerStar = List(5) {
                    Rating.Star(it + 1, Random.nextLong(10_000, 1_000_000))
                }.sortedByDescending { it.star },
            )
            val storeRating = Rating(
                average = 4.5f,
                totalPerStar = List(5) {
                    Rating.Star(it + 1, Random.nextLong(10_000, 100_000))
                }.sortedByDescending { it.star },
            )
            val selectedFilters = ReviewStatisticsFilters(
                year = 2022,
                month = Month.entries.random(),
            )
            val selectedCategory = ReviewCategory(name = "Staff friendliness")
            val categories = listOf(
                selectedCategory,
                ReviewCategory(name = "Ambience"),
                ReviewCategory(name = "Cleanliness"),
            )
            return sequenceOf(
                ReviewsUiState(
                    shopRating = shopRating,
                    storeRating = storeRating,
                    categories = categories,
                    selectedFilters = selectedFilters,
                    selectedCategory = selectedCategory,
                    statisticsResponse = StatisticsResponse.Success(
                        data = ReviewCategory.Statistics(
                            totalReviews = 100,
                            generalSentiment = ReviewCategory.Statistics.GeneralSentiment.Positive,
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
                        barGraphData = StatisticsResponse.Success.BarGraphData(
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
                ),
                ReviewsUiState(
                    shopRating = shopRating,
                    storeRating = storeRating,
                    categories = categories,
                    selectedFilters = selectedFilters,
                    selectedCategory = selectedCategory,
                    statisticsResponse = StatisticsResponse.Loading,
                ),
                ReviewsUiState(
                    shopRating = shopRating,
                    storeRating = storeRating,
                    categories = categories,
                    selectedFilters = selectedFilters,
                    selectedCategory = selectedCategory,
                    statisticsResponse = StatisticsResponse.Failure(
                        error = UiText.DynamicString("Example error message"),
                        type = DataError.Network.UNKNOWN,
                    ),
                ),
                ReviewsUiState(
                    shopRating = shopRating,
                    storeRating = storeRating,
                    categories = categories,
                    selectedFilters = selectedFilters,
                    selectedCategory = selectedCategory,
                ),
            )
        }
}

@XentlyPreview
@Composable
private fun ReviewsScreenPreview(
    @PreviewParameter(ReviewsUiStateProvider::class)
    state: ReviewsUiState,
) {
    XentlyTheme {
        ReviewsScreen(
            state = state,
            event = null,
            statisticsResponse = null,
            onClickBack = {},
            onAction = {},
            onClickAddNewReviewCategory = {},
        )
    }
}