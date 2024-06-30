package co.ke.xently.features.profile.presentation.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun StatisticSummaryCard(
    modifier: Modifier,
    stat: String,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        } else {
            CardDefaults.cardColors()
        },
        content = {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            ) {
                Column {
                    Text(
                        text = stat,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        },
    )
}

@XentlyThemePreview
@Composable
private fun StatisticSummaryCardPreview() {
    XentlyTheme {
        StatisticSummaryCard(
            modifier = Modifier,
            stat = "12",
            title = "Reviews",
            isSelected = false,
            onClick = {},
        )
    }
}