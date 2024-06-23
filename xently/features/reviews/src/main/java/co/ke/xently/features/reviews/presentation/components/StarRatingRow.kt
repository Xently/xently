package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.domain.StarRating
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark
import kotlin.math.floor


private fun Number.getRatings(maxStarRating: Int): Array<StarRating> {
    val ratings = Array(maxStarRating) {
        StarRating.Empty
    }
    val ratingAsFloat = toFloat()
    if (ratingAsFloat <= 0) return ratings

    val numberOfFullRatings = floor(ratingAsFloat).toInt()
    for (index in 0 until numberOfFullRatings) {
        ratings[index] = StarRating.Full
    }

    val unclassifiedRating = ratingAsFloat - numberOfFullRatings
    if (unclassifiedRating <= 0) return ratings
    if (unclassifiedRating <= 1) {
        ratings[numberOfFullRatings] = StarRating.Half
    }
    return ratings
}

@Composable
internal fun StarRatingRow(
    isDark: Boolean,
    average: Float,
    maximumStarRating: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        average.getRatings(maximumStarRating).forEachIndexed { index, starRating ->
            val icon = when (starRating) {
                StarRating.Full -> Icons.Default.Star
                StarRating.Half -> Icons.AutoMirrored.Filled.StarHalf
                StarRating.Empty -> Icons.Default.Star
            }
            Icon(
                icon,
                tint = starRating.tint(isDark),
                contentDescription = stringResource(R.string.star, index + 1),
            )
        }
    }
}

private data class StarRatingRowState(
    val average: Float,
    val maximumStarRating: Int = 5,
)

private class StarRatingRowPreviewProvider : PreviewParameterProvider<StarRatingRowState> {
    override val values: Sequence<StarRatingRowState>
        get() = sequenceOf(
            StarRatingRowState(average = 0f),
            StarRatingRowState(average = 0.5f),
            StarRatingRowState(average = 1f),
            StarRatingRowState(average = 4.5f),
            StarRatingRowState(average = 5f),
        )
}

@XentlyThemePreview
@Composable
private fun StarRatingRowPreview(
    @PreviewParameter(StarRatingRowPreviewProvider::class)
    state: StarRatingRowState,
) {
    XentlyTheme {
        StarRatingRow(
            modifier = Modifier.padding(16.dp),
            average = state.average,
            isDark = LocalThemeIsDark.current.value,
            maximumStarRating = state.maximumStarRating,
        )
    }
}