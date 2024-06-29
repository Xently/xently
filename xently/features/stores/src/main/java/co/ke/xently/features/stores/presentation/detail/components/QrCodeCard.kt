package co.ke.xently.features.stores.presentation.detail.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.stores.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.components.shimmer

@Composable
internal fun QrCodeCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onGetPointsAndReviewClick: () -> Unit,
) {
    Card(shape = RectangleShape, modifier = modifier) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(
                    text = stringResource(R.string.message_click_to_get_review_points),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.shimmer(isLoading),
                )
            },
            trailingContent = {
                Button(
                    onClick = onGetPointsAndReviewClick,
                    modifier = Modifier.shimmer(isLoading),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.action_label_review_to_get_points),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            },
        )

        Text(
            text = stringResource(R.string.message_review_requirement),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .padding(horizontal = 16.dp)
                .shimmer(isLoading),
        )
    }
}

internal class BooleanParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(false, true)
}

@XentlyThemePreview
@Composable
private fun QrCodeCardPreview(
    @PreviewParameter(BooleanParameterProvider::class)
    isLoading: Boolean,
) {
    XentlyTheme {
        QrCodeCard(isLoading = isLoading) {}
    }
}
