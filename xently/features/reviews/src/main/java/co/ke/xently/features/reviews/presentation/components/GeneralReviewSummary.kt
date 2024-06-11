package co.ke.xently.features.reviews.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Rating
import co.ke.xently.features.reviews.data.domain.error.DataError.Network
import co.ke.xently.features.reviews.presentation.reviews.ReviewSummaryResponse
import co.ke.xently.features.reviews.presentation.theme.xentlyLinearProgressLight
import co.ke.xently.features.reviews.presentation.theme.xentlyLinearProgressLightTrack
import co.ke.xently.features.reviews.presentation.utils.UiText
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.domain.coolFormat
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark
import com.valentinilk.shimmer.shimmer
import kotlin.random.Random

@Composable
internal fun GeneralReviewSummary(
    modifier: Modifier,
    headline: String,
    response: ReviewSummaryResponse,
    onClickRetry: () -> Unit,
) {
    val isDark by LocalThemeIsDark.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        UnderlinedHeadline(
            modifier = Modifier.fillMaxWidth(),
            headline = headline,
        )

        when (response) {
            is ReviewSummaryResponse.Failure -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = response.error.asString(),
                        modifier = Modifier.weight(1f),
                    )
                    if (response.type is Network) {
                        Button(onClick = onClickRetry) {
                            Text(text = stringResource(R.string.action_retry))
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
                modifier = if (isLoading) Modifier.shimmer() else Modifier,
            )
            StarRatingRow(
                isDark = isDark,
                average = rating.average,
                maximumStarRating = rating.maxStarRating,
                modifier = if (isLoading) Modifier.shimmer() else Modifier,
            )
            Text(
                text = "Total - ${rating.totalReviews.coolFormat()}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Light,
                modifier = if (isLoading) Modifier.shimmer() else Modifier,
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
                        modifier = if (isLoading) Modifier.shimmer() else Modifier,
                    )
                    val progress = rememberSaveable(star.count, rating.totalReviews) {
                        (star.count / rating.totalReviews.toDouble()).toFloat()
                    }
                    val animatedProgress by animateFloatAsState(
                        progress,
                        label = "progress-animation",
                    )
                    if (isDark) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = (if (isLoading) Modifier.shimmer() else Modifier)
                                .weight(1f)
                                .height(12.dp),
                            strokeCap = StrokeCap.Round,
                        )
                    } else {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = (if (isLoading) Modifier.shimmer() else Modifier)
                                .weight(1f)
                                .height(12.dp),
                            color = xentlyLinearProgressLight,
                            trackColor = xentlyLinearProgressLightTrack,
                            strokeCap = StrokeCap.Round,
                        )
                    }
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
                type = Network.UNKNOWN,
            ),
            ReviewSummaryResponse.Success(
                Rating(
                    average = 4.5f,
                    totalPerStar = List(5) {
                        Rating.Star(it + 1, Random.nextLong(100))
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