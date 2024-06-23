package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview


@Composable
internal fun StatisticSummaryCard(
    modifier: Modifier,
    stat: String,
    title: String,
    statColor: Color,
) {
    OutlinedCard(modifier = modifier) {
        Text(
            text = stat,
            color = statColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
        )
        HorizontalDivider(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@XentlyThemePreview
@Composable
private fun StatisticSummaryCardPreview() {
    XentlyTheme {
        StatisticSummaryCard(
            modifier = Modifier.padding(16.dp),
            stat = "100",
            title = "Followers",
            statColor = MaterialTheme.colorScheme.primary,
        )
    }
}