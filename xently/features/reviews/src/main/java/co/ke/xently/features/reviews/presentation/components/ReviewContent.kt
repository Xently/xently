package co.ke.xently.features.reviews.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.data.domain.error.DataError
import co.ke.xently.features.reviews.presentation.reviews.BarGraphData
import co.ke.xently.features.reviews.presentation.reviews.StatisticsResponse
import co.ke.xently.features.reviews.presentation.theme.STAR_RATING_COLOURS
import co.ke.xently.features.reviews.presentation.utils.UiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import com.aay.compose.barChart.model.BarParameters
import kotlinx.datetime.Month
import kotlin.random.Random

@Composable
internal fun ReviewContent(
    modifier: Modifier,
    category: ReviewCategory,
    filters: ReviewStatisticsFilters,
    response: StatisticsResponse,
    onClickRetry: () -> Unit,
    onClickViewComments: () -> Unit,
    onClickApplyFilters: () -> Unit,
    onClickSelectYear: (Int) -> Unit,
    onClickRemoveYear: (Int) -> Unit,
    onClickSelectMonth: (Month) -> Unit,
    onClickRemoveMonth: (Month) -> Unit,
) {
    AnimatedContent(response, modifier = modifier, label = "reviews") {
        when (it) {
            is StatisticsResponse.Failure -> {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = it.error.asString(),
                        modifier = Modifier.weight(1f),
                    )
                    if (it.type is DataError.Network.Retryable) {
                        Button(onClick = onClickRetry) {
                            Text(text = stringResource(R.string.action_retry))
                        }
                    }
                }
            }

            StatisticsResponse.Loading -> {
                StatisticsSuccessContent(
                    category = category,
                    success = StatisticsResponseSuccess,
                    filters = filters,
                    isLoading = true,
                    onClickSelectYear = {},
                    onClickSelectMonth = {},
                    onClickApplyFilters = {},
                    onClickViewComments = {},
                    onClickRemoveMonth = {},
                    onClickRemoveYear = {},
                )
            }

            is StatisticsResponse.Success -> {
                StatisticsSuccessContent(
                    category = category,
                    success = it,
                    filters = filters,
                    onClickSelectYear = onClickSelectYear,
                    onClickSelectMonth = onClickSelectMonth,
                    onClickApplyFilters = onClickApplyFilters,
                    onClickViewComments = onClickViewComments,
                    onClickRemoveMonth = onClickRemoveMonth,
                    onClickRemoveYear = onClickRemoveYear,
                )
            }
        }
    }
}

private val StatisticsResponseSuccess = StatisticsResponse.Success(
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
)

private data class ReviewContentState(
    val response: StatisticsResponse,
)

private class ReviewContentPreviewProvider : PreviewParameterProvider<ReviewContentState> {
    override val values: Sequence<ReviewContentState>
        get() = sequenceOf(
            ReviewContentState(response = StatisticsResponseSuccess),
            ReviewContentState(response = StatisticsResponse.Loading),
            ReviewContentState(
                response = StatisticsResponse.Failure(
                    error = UiText.DynamicString("Example error message"),
                    type = DataError.Network.Retryable.Unknown,
                ),
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ReviewContentPreview(
    @PreviewParameter(ReviewContentPreviewProvider::class)
    state: ReviewContentState,
) {
    XentlyTheme {
        ReviewContent(
            modifier = Modifier.padding(16.dp),
            category = ReviewCategory(name = "Staff friendliness"),
            filters = ReviewStatisticsFilters(
                year = 2022,
                month = Month.entries.random(),
            ),
            response = state.response,
            onClickRetry = {},
            onClickViewComments = {},
            onClickApplyFilters = {},
            onClickSelectYear = {},
            onClickSelectMonth = {},
            onClickRemoveMonth = {},
            onClickRemoveYear = {},
        )
    }
}