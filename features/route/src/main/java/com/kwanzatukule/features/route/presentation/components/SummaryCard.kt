package com.kwanzatukule.features.route.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@Composable
internal fun SummaryCard(summary: Summary) {
    Card(modifier = Modifier.size(150.dp)) {
        Text(
            text = summary.label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = summary.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@KwanzaPreview
@Composable
private fun SummaryCardPreview() {
    KwanzaTukuleTheme {
        SummaryCard(
            summary = Summary(
                label = "Booked Order",
                value = "10,000"
            )
        )
    }
}