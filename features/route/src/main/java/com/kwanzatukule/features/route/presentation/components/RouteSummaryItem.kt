package com.kwanzatukule.features.route.presentation.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.core.domain.formatNumber
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlin.random.Random


data class Summary(
    val label: String,
    val value: String,
)

@Composable
fun RouteSummary.rememberSummaries(): List<Summary> {
    return remember(this) {
        listOf(
            Summary(
                label = "Booked Order",
                value = numberOfCustomers.formatNumber(),
            ),
            Summary(
                label = "Variance",
                value = variance.formatNumber(),
            ),
            Summary(
                label = "No. of Customers",
                value = numberOfCustomers.formatNumber(),
            ),
            Summary(
                label = "Total route Customers",
                value = totalRouteCustomers.formatNumber(),
            ),
            Summary(
                label = "Geographical Distance",
                value = geographicalDistance.formatNumber(),
            ),
        )
    }
}

@Composable
fun RouteSummaryItem(route: Route) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            ),
            leadingContent = {
                Icon(
                    Icons.Default.Route,
                    contentDescription = null,
                )
            },
            headlineContent = {
                Text(
                    text = "Route Summary",
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold,
                )
            },
            supportingContent = {
                Text(text = route.name)
            },
        )
        route.summary?.let { summary ->
            RouteSummaryLazyRow(summary = summary)
        }
    }
}

@Composable
fun RouteSummaryLazyRow(summary: RouteSummary, modifier: Modifier = Modifier) {
    val summaries = summary.rememberSummaries()
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(summaries, key = { it.label }) { summary ->
            SummaryCard(summary = summary)
        }
    }
}

@KwanzaPreview
@Composable
private fun RouteSummaryItemPreview() {
    KwanzaTukuleTheme {
        val route = Route(
            id = 1,
            name = "Kibera",
            description = "Kibera route description...",
            summary = RouteSummary(
                bookedOrder = Random.nextInt(100),
                variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                numberOfCustomers = Random.nextInt(100),
                totalRouteCustomers = Random.nextInt(100),
                geographicalDistance = Random.nextInt(1_000, 10_000),
            ),
        )
        RouteSummaryItem(route = route)
    }
}