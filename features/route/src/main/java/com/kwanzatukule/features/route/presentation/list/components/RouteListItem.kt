package com.kwanzatukule.features.route.presentation.list.components


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.route.presentation.components.RouteSummaryLazyRow
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RouteListItem(
    route: Route,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RectangleShape,
        colors = CardDefaults.outlinedCardColors(),
    ) {
        ListItem(
            leadingContent = {
                Card(
                    modifier = Modifier.size(60.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {

                }
            },

            headlineContent = { Text(text = route.name) },
            supportingContent = { Text(text = route.description) },
            trailingContent = trailingContent
        )
        if (LocalCanViewRouteSummary.current && route.summary != null) {
            var seeMore by rememberSaveable(route.id) { mutableStateOf(false) }

            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleMedium) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(.85f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.Route, contentDescription = null)
                        Text(
                            text = "Route Summary",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.basicMarquee(),
                            style = MaterialTheme.typography.labelLarge,
                            textDecoration = TextDecoration.Underline,
                        )
                    }

                    IconButton(onClick = { seeMore = !seeMore }) {
                        AnimatedContent(targetState = seeMore, label = "Expand/Collapse more") {
                            if (it) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "See less",
                                )
                            } else {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "See more",
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = seeMore) {
                RouteSummaryLazyRow(summary = route.summary!!)
            }
        }
    }
}

@KwanzaPreview
@Composable
private fun RouteListItemPreview() {
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
        RouteListItem(route = route)
    }
}