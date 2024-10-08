package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.error.ConfigurationError
import co.ke.xently.features.reviews.data.domain.error.DataError.Network
import co.ke.xently.features.reviews.data.domain.error.UnknownError
import co.ke.xently.features.reviews.presentation.reviews.ReviewSummaryResponse
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.core.domain.coolFormat
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.fade
import com.google.accompanist.placeholder.material3.placeholder
import kotlin.random.Random

@Composable
internal fun GeneralReviewSummary(
    modifier: Modifier,
    headline: String,
    response: ReviewSummaryResponse,
    onClickRetry: () -> Unit,
) {
    val isDark by LocalThemeIsDark.current
    val eventHandler = LocalEventHandler.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        UnderlinedHeadline(modifier = Modifier.fillMaxWidth(), headline = headline)

        when (response) {
            is ReviewSummaryResponse.Failure -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = response.error.asString(), modifier = Modifier.weight(1f))
                    when (response.type) {
                        ConfigurationError.ShopSelectionRequired -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = eventHandler::requestShopSelection) {
                                Text(text = stringResource(R.string.action_select_shop))
                            }
                        }

                        ConfigurationError.StoreSelectionRequired -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = eventHandler::requestStoreSelection) {
                                Text(text = stringResource(R.string.action_select_store))
                            }
                        }

                        is Network.Unauthorized -> {
                            Spacer(modifier = Modifier.height(16.dp))

                            LoginAndRetryButtonsRow(onRetry = onClickRetry)
                        }

                        else -> {
                            Button(onClick = onClickRetry) {
                                Text(text = stringResource(R.string.action_retry))
                            }
                        }
                    }
                }
            }

            ReviewSummaryResponse.Loading -> {
                val rating = remember {
                    Rating(
                        average = 4.5f,
                        totalPerStar = List(5) {
                            Rating.Star(it + 1, Random.nextLong(100))
                        }.sortedByDescending { it.star },
                    )
                }
                ReviewSummaryContent(
                    rating = rating,
                    isDark = isDark,
                    isLoading = true,
                )
            }

            is ReviewSummaryResponse.Success -> {
                ReviewSummaryContent(
                    isDark = isDark,
                    rating = response.data,
                )
            }
        }
    }
}

@Composable
private fun ReviewSummaryContent(rating: Rating, isDark: Boolean, isLoading: Boolean = false) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = rating.average.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
            )
            StarRatingRow(
                isDark = isDark,
                average = rating.average,
                maximumStarRating = rating.maxStarRating,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
            )
            Text(
                text = stringResource(
                    R.string.summary_review_total,
                    rating.totalReviews.coolFormat(),
                ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Light,
                modifier = Modifier.placeholder(
                    visible = isLoading,
                    highlight = PlaceholderHighlight.fade(),
                ),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .height(IntrinsicSize.Max),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            for (star in rating.totalPerStar) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = star.star.toString(),
                        modifier = Modifier.placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.fade(),
                        ),
                    )
                    val progress = rememberSaveable(star.count, rating.totalReviews) {
                        (star.count / rating.totalReviews.toDouble()).toFloat()
                    }
                    StarRatingLinearProgress(
                        progress = progress,
                        isDark = isDark,
                        modifier = Modifier
                            .weight(1f)
                            .placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.fade(),
                            ),
                    )
                }
            }
        }
    }
}

private class ReviewSummaryResponsePreviewParameterProvider :
    PreviewParameterProvider<ReviewSummaryResponse> {
    override val values: Sequence<ReviewSummaryResponse>
        get() = sequenceOf(
            ReviewSummaryResponse.Loading,
            ReviewSummaryResponse.Failure(
                error = UiText.DynamicString("Sample error message"),
                type = UnknownError,
            ),
            ReviewSummaryResponse.Failure(
                error = UiText.DynamicString("Sample error message"),
                type = Network.Unauthorized,
            ),
            ReviewSummaryResponse.Success(
                Rating(
                    average = 4.5f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, Random.nextLong(100))
                    }.sortedByDescending { it.star },
                )
            ),
            ReviewSummaryResponse.Success(
                Rating(
                    average = 0f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, 0)
                    }.sortedByDescending { it.star },
                )
            ),
        )

}

@XentlyThemePreview
@Composable
private fun GeneralReviewSummaryPreview(
    @PreviewParameter(ReviewSummaryResponsePreviewParameterProvider::class)
    response: ReviewSummaryResponse,
) {
    XentlyTheme {
        GeneralReviewSummary(
            modifier = Modifier.padding(16.dp),
            headline = "Headline",
            response = response,
            onClickRetry = {},
        )
    }
}