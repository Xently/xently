package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.valentinilk.shimmer.shimmer
import kotlinx.datetime.Month

@Composable
internal fun StatisticsSuccessContent(
    modifier: Modifier = Modifier,
    category: ReviewCategory,
    filters: ReviewStatisticsFilters,
    success: StatisticsResponse.Success,
    isLoading: Boolean = false,
    onClickViewComments: () -> Unit,
    onClickApplyFilters: () -> Unit,
    onClickSelectYear: (Int) -> Unit,
    onClickRemoveYear: (Int) -> Unit,
    onClickSelectMonth: (Month) -> Unit,
    onClickRemoveMonth: (Month) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        UnderlinedHeadline(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            dividerSpace = 0.dp,
            headline = stringResource(R.string.reviews_statistics_title),
            trailingContent = {
                TextButton(
                    modifier = if (isLoading) Modifier.shimmer() else Modifier,
                    enabled = !isLoading,
                    onClick = { onClickViewComments() },
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            item(key = "total-reviews", contentType = "total-reviews") {
                StatisticSummaryCard(
                    modifier = (if (isLoading) Modifier.shimmer() else Modifier).width(120.dp),
                    stat = success.data.totalReviews.coolFormat(),
                    title = stringResource(R.string.reviews_statistics_total_reviews_title),
                    statColor = MaterialTheme.colorScheme.primary,
                )
            }
            item(key = "general-sentiment", contentType = "general-sentiment") {
                StatisticSummaryCard(
                    modifier = (if (isLoading) Modifier.shimmer() else Modifier).width(120.dp),
                    stat = success.data.generalSentiment.text,
                    title = stringResource(R.string.reviews_statistics_general_sentiments_title),
                    statColor = when (success.data.generalSentiment) {
                        ReviewCategory.Statistics.GeneralSentiment.Positive -> Color.Green
                        ReviewCategory.Statistics.GeneralSentiment.Negative -> Color.Red
                    },
                )
            }
            item(key = "percentage-satisfaction", contentType = "percentage-satisfaction") {
                StatisticSummaryCard(
                    modifier = (if (isLoading) Modifier.shimmer() else Modifier).width(120.dp),
                    stat = "${success.data.percentageSatisfaction}%",
                    title = stringResource(R.string.reviews_statistics_percentage_satisfaction_title),
                    statColor = MaterialTheme.colorScheme.primary,
                )
            }
            item(key = "average-rating", contentType = "average-rating") {
                StatisticSummaryCard(
                    modifier = (if (isLoading) Modifier.shimmer() else Modifier).width(120.dp),
                    stat = success.data.averageRating.toString(),
                    title = stringResource(R.string.reviews_statistics_average_rating_title),
                    statColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
        var showFilters by rememberSaveable { mutableStateOf(false) }
        val selectedYear = filters.year
        val selectedMonth = filters.month
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = (if (isLoading) Modifier.shimmer() else Modifier),
                )
                val text = remember(filters) {
                    buildString {
                        selectedMonth?.let { month ->
                            month.name.toLowerCase(Locale.current).replaceFirstChar {
                                it.uppercaseChar()
                            }.also(::append)
                            append(' ')
                        }
                        if (selectedYear != null) {
                            append(selectedYear)
                        }
                    }.trim()
                }
                if (text.isNotBlank()) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = (if (isLoading) Modifier.shimmer() else Modifier),
                    )
                }
            }
            TextButton(
                enabled = !isLoading,
                modifier = (if (isLoading) Modifier.shimmer() else Modifier),
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
            StatisticsFilters(
                modifier = Modifier.padding(horizontal = 16.dp),
                years = success.data.years,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                onClickSelectYear = onClickSelectYear,
                onClickSelectMonth = onClickSelectMonth,
                onClickApplyFilters = { showFilters = false; onClickApplyFilters() },
                onClickRemoveYear = onClickRemoveYear,
                onClickRemoveMonth = onClickRemoveMonth,
            )
        }

        if (success.data.groupedStatistics.isNotEmpty()) {
            Box(
                modifier = (if (isLoading) Modifier.shimmer() else Modifier)
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(horizontal = 16.dp),
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