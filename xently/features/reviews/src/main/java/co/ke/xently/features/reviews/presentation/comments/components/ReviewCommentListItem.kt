package co.ke.xently.features.reviews.presentation.comments.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.features.ui.core.presentation.theme.shimmer
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReviewCommentListItem(
    review: Review,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    ElevatedCard(modifier = modifier) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = {
                Card(
                    modifier = Modifier
                        .size(60.dp)
                        .shimmer(isLoading)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                }
            },
            headlineContent = {
                Text(
                    text = review.reviewerName ?: stringResource(R.string.anonymous),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.shimmer(isLoading),
                )
            },
            supportingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = pluralStringResource(
                            R.plurals.star_rating,
                            review.starRating,
                            review.starRating,
                        ),
                        fontWeight = FontWeight.Light,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.shimmer(isLoading),
                    )

                    Icon(
                        Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(horizontal = 8.dp)
                            .shimmer(isLoading),
                    )

                    val timePickerState = rememberTimePickerState()

                    Text(
                        fontWeight = FontWeight.Light,
                        style = MaterialTheme.typography.labelLarge,
                        text = remember(review.dateOfReview(), timePickerState) {
                            review.dateOfReview()
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .let {
                                    val time = Time(hour = it.time.hour, minute = it.time.minute)
                                    "${it.date} - ${time.toString(timePickerState.is24hour)}"
                                }
                        },
                        modifier = Modifier.shimmer(isLoading),
                    )
                }
            },
            trailingContent = {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier.shimmer(isLoading),
                )
            },
        )
        var expand by rememberSaveable { mutableStateOf(false) }
        Text(
            text = review.message,
            maxLines = if (expand) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .shimmer(isLoading)
                .clickable(
                    role = Role.Checkbox,
                    indication = ripple(radius = 1_000.dp),
                    interactionSource = remember { MutableInteractionSource() },
                ) { expand = !expand },
        )
    }
}

private data class ReviewListItemParameter(
    val review: Review,
    val isLoading: Boolean = false,
)

private class ReviewListItemParameterProvider : PreviewParameterProvider<ReviewListItemParameter> {
    override val values: Sequence<ReviewListItemParameter>
        get() = sequenceOf(
            ReviewListItemParameter(
                Review(
                    starRating = Random.nextInt(1, 6),
                    message = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                    reviewerName = "John Doe",
                    links = mapOf(
                        "self" to Link("https://jsonplaceholder.typicode.com/posts/1")
                    ),
                ),
            ),
            ReviewListItemParameter(
                Review(
                    starRating = Random.nextInt(1, 6),
                    message = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                    reviewerName = "John Doe",
                    links = mapOf(
                        "self" to Link("https://jsonplaceholder.typicode.com/posts/1")
                    ),
                ),
                isLoading = true,
            ),
            ReviewListItemParameter(
                Review(
                    starRating = Random.nextInt(1, 6),
                    message = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                    reviewerName = null,
                    links = mapOf(
                        "self" to Link("https://jsonplaceholder.typicode.com/posts/1")
                    ),
                )
            ),
            ReviewListItemParameter(
                Review(
                    starRating = Random.nextInt(1, 6),
                    message = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                    reviewerName = null,
                    links = mapOf(
                        "self" to Link("https://jsonplaceholder.typicode.com/posts/1")
                    ),
                ),
                isLoading = true,
            ),
        )
}

@XentlyThemePreview
@Composable
private fun ReviewListItemPreview(
    @PreviewParameter(ReviewListItemParameterProvider::class)
    parameter: ReviewListItemParameter,
) {
    XentlyTheme {
        ReviewCommentListItem(
            review = parameter.review,
            isLoading = parameter.isLoading,
            modifier = Modifier.padding(8.dp),
        )
    }
}