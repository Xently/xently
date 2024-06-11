package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviews.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import kotlinx.datetime.Month

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun StatisticsFilters(
    years: List<Int>,
    selectedYear: Int?,
    selectedMonth: Month?,
    modifier: Modifier = Modifier,
    onClickApplyFilters: () -> Unit,
    onClickSelectYear: (Int) -> Unit,
    onClickRemoveYear: (Int) -> Unit,
    onClickSelectMonth: (Month) -> Unit,
    onClickRemoveMonth: (Month) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.year_filter_headline),
            textDecoration = TextDecoration.Underline,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (year in years) {
                FilterChip(
                    selected = year == selectedYear,
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
                    trailingIcon = if (year != selectedYear) null else {
                        {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Unselect year - $selectedYear",
                                modifier = Modifier
                                    .size(InputChipDefaults.AvatarSize)
                                    .clickable(onClick = { onClickRemoveYear(year) }),
                            )
                        }
                    },
                )
            }
        }

        if (selectedYear != null) {
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
                        selected = month == selectedMonth,
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
                        trailingIcon = if (month != selectedMonth) null else {
                            {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Unselect month - $selectedMonth",
                                    modifier = Modifier
                                        .size(InputChipDefaults.AvatarSize)
                                        .clickable(onClick = { onClickRemoveMonth(month) }),
                                )
                            }
                        },
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onClickApplyFilters,
            content = {
                Text(text = stringResource(R.string.button_label_apply_filters))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
        )
    }
}

@XentlyThemePreview
@Composable
private fun StatisticsFiltersPreview() {
    XentlyTheme {
        val years = remember { List(15) { 2014 + it } }
        StatisticsFilters(
            years = years,
            selectedYear = years.randomOrNull(),
            selectedMonth = Month.entries.randomOrNull(),
            modifier = Modifier.padding(16.dp),
            onClickApplyFilters = {},
            onClickSelectYear = {},
            onClickRemoveYear = {},
            onClickSelectMonth = {},
            onClickRemoveMonth = {},
        )
    }
}