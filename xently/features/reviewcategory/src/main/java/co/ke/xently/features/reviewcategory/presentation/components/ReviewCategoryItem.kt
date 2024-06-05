package co.ke.xently.features.reviewcategory.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
fun ReviewCategoryItem(
    modifier: Modifier,
    category: ReviewCategory,
    selected: Boolean,
    onClick: () -> Unit,
    onClickMoreOptions: () -> Unit,
) {
    Surface(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.small.copy(CornerSize(8.dp)),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(PaddingValues(start = 16.dp)),
            ) {
                AnimatedVisibility(selected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
                Text(
                    text = category.name,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onClickMoreOptions,
                    content = {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                        )
                    },
                )
            }
        },
    )
}

private data class ReviewCategoryItemState(
    val category: ReviewCategory,
    val selected: Boolean,
)

private class ReviewCategoryItemStateParameterProvider :
    PreviewParameterProvider<ReviewCategoryItemState> {
    override val values: Sequence<ReviewCategoryItemState>
        get() = sequenceOf(
            ReviewCategoryItemState(
                selected = true,
                category = ReviewCategory(
                    name = "Test",
                ),
            ),
            ReviewCategoryItemState(
                selected = false,
                category = ReviewCategory(
                    name = "Test",
                ),
            ),
        )
}


@XentlyThemePreview
@Composable
private fun ReviewCategoryItemPreview(
    @PreviewParameter(ReviewCategoryItemStateParameterProvider::class)
    state: ReviewCategoryItemState,
) {
    XentlyTheme {
        ReviewCategoryItem(
            modifier = Modifier,
            category = state.category,
            selected = state.selected,
            onClick = {},
            onClickMoreOptions = {},
        )
    }
}