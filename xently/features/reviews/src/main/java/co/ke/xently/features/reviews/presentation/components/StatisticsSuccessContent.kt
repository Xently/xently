package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.ReviewStatisticsFilters
import co.ke.xently.features.reviews.presentation.reviews.StatisticsResponse
import co.ke.xently.libraries.ui.core.domain.coolFormat
import com.aay.compose.barChart.BarChart
import kotlinx.datetime.Month

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StatisticsSuccessContent(
    modifier: Modifier = Modifier,
    category: ReviewCategory,
    filters: ReviewStatisticsFilters,
    success: StatisticsResponse.Success,
    onClickViewComments: () -> Unit,
    onClickApplyFilters: () -> Unit,
    onClickSelectYear: (Int) -> Unit,
    onClickSelectMonth: (Month) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        UnderlinedHeadline(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            dividerSpace = 0.dp,
            headline = stringResource(R.string.reviews_statistics_title),
            trailingContent = {
                TextButton(
                    onClick = {
                        onClickViewComments()
                    },
                    content = {
                        Text(
                            text = stringResource(R.string.button_label_view_comments),
                            textDecoration = TextDecoration.Underline,
                        )
                    },
                    contentPadding = PaddingValues(vertical = 12.dp),
                    shape = RoundedCornerShape(20),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                )
            },
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
        ) {
            StatisticSummaryCard(
                modifier = Modifier.width(120.dp),
                stat = success.data.totalReviews.coolFormat(),
                title = stringResource(R.string.reviews_statistics_total_reviews_title),
                statColor = MaterialTheme.colorScheme.primary,
            )
            StatisticSummaryCard(
                modifier = Modifier.width(120.dp),
                stat = success.data.generalSentiment.text,
                title = stringResource(R.string.reviews_statistics_general_sentiments_title),
                statColor = when (success.data.generalSentiment) {
                    ReviewCategory.Statistics.GeneralSentiment.Positive -> Color.Green
                    ReviewCategory.Statistics.GeneralSentiment.Negative -> Color.Red
                },
            )
            StatisticSummaryCard(
                modifier = Modifier.width(120.dp),
                stat = "${success.data.percentageSatisfaction}%",
                title = stringResource(R.string.reviews_statistics_percentage_satisfaction_title),
                statColor = MaterialTheme.colorScheme.primary,
            )
            StatisticSummaryCard(
                modifier = Modifier.width(120.dp),
                stat = success.data.averageRating.toString(),
                title = stringResource(R.string.reviews_statistics_average_rating_title),
                statColor = MaterialTheme.colorScheme.primary,
            )
        }
        var showFilters by rememberSaveable {
            mutableStateOf(false)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                )
                val text = remember(filters) {
                    buildString {
                        filters.month?.let { month ->
                            month.name.toLowerCase(Locale.current).replaceFirstChar {
                                it.uppercaseChar()
                            }.also(::append)
                            append(' ')
                        }
                        if (filters.year != null) {
                            append(filters.year)
                        }
                    }.trim()
                }
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            TextButton(
                onClick = {
                    showFilters = !showFilters
                },
                content = {
                    Text(
                        text = stringResource(R.string.button_label_filter_by_date),
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Light,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val icon = if (showFilters) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    }
                    Icon(
                        icon,
                        contentDescription = null,
                    )
                },
                contentPadding = PaddingValues(vertical = 12.dp),
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
            )
        }

        if (showFilters) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.year_filter_headline),
                    textDecoration = TextDecoration.Underline,
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (year in success.data.years) {
                        FilterChip(
                            selected = year == filters.year,
                            onClick = {
                                onClickSelectYear(year)
                            },
                            label = {
                                Text(text = year.toString())
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                    }
                }

                if (filters.year != null) {
                    Text(
                        text = stringResource(R.string.month_filter_headline),
                        textDecoration = TextDecoration.Underline,
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        for (month in Month.entries) {
                            FilterChip(
                                selected = month == filters.month,
                                onClick = {
                                    onClickSelectMonth(month)
                                },
                                label = {
                                    Text(text = month.toString())
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                            )
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        showFilters = false
                        onClickApplyFilters()
                    },
                    content = {
                        Text(text = stringResource(R.string.button_label_apply_filters))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                )
            }
        }

        if (success.data.groupedStatistics.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                BarChart(
//                    yAxisRange = 15,
                    barWidth = 20.dp,
                    showGridWithSpacer = false,
                    xAxisData = success.barGraphData.xAxis,
                    chartParameters = success.barGraphData.barParameters,
                    descriptionStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                    yAxisStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                    xAxisStyle = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onBackground),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(16.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painterResource(co.ke.xently.features.ui.core.R.drawable.empty),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.message_no_graph_data),
                    modifier = modifier,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { showFilters = true }) {
                    Text(text = stringResource(R.string.button_label_adjust_filters))
                }
            }
        }
    }
}