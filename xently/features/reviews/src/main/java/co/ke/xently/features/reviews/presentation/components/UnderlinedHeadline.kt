package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview


@Composable
internal fun UnderlinedHeadline(
    headline: String,
    modifier: Modifier = Modifier,
    dividerSpace: Dp = 8.dp,
    verticalAlignment: Alignment.Vertical = Alignment.Bottom,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dividerSpace),
    ) {
        Row(
            verticalAlignment = verticalAlignment,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = headline,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.SemiBold,
            )
            trailingContent()
        }
        HorizontalDivider()
    }
}

private data class UnderlinedHeadlineState(
    val trailingContent: @Composable RowScope.() -> Unit = {},
)

private class UnderlinedHeadlinePreviewProvider :
    PreviewParameterProvider<UnderlinedHeadlineState> {
    override val values: Sequence<UnderlinedHeadlineState>
        get() = sequenceOf(
            UnderlinedHeadlineState(),
            UnderlinedHeadlineState {
                Text(text = "Favourite")
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                )
            },
        )
}

@XentlyThemePreview
@Composable
private fun UnderlinedHeadlinePreview(
    @PreviewParameter(UnderlinedHeadlinePreviewProvider::class)
    state: UnderlinedHeadlineState,
) {
    XentlyTheme {
        UnderlinedHeadline(
            headline = "Headline title",
            trailingContent = state.trailingContent,
            modifier = Modifier.padding(16.dp),
        )
    }
}