package co.ke.xently.features.stores.presentation.moredetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.domain.isCurrentlyOpen
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import kotlinx.datetime.DayOfWeek


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OpeningHourItem(
    openingHour: OpeningHour,
    dayOfWeekToday: DayOfWeek,
    timePickerState: TimePickerState,
    isLoading: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val isCurrentlyOpen = rememberIsCurrentlyOpen(
            openingHour = openingHour,
            dayOfWeekToday = dayOfWeekToday,
        )
        val color = when (isCurrentlyOpen) {
            true -> Color.Green
            false -> MaterialTheme.colorScheme.error
            null -> Color.Unspecified
        }
        Text(
            color = color,
            modifier = Modifier.placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
            ),
            text = buildString {
                append(
                    openingHour.dayOfWeek.name.toLowerCase(
                        Locale.current
                    ).replaceFirstChar(Char::uppercaseChar)
                )
                append(":")
            },
        )

        Text(
            color = color,
            modifier = Modifier.placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
            ),
            text = remember(openingHour.openTime, openingHour.closeTime, timePickerState) {
                buildString {
                    append(openingHour.openTime.toString(timePickerState.is24hour))
                    append(" - ")
                    append(openingHour.closeTime.toString(timePickerState.is24hour))
                }
            },
        )
    }
}

@Composable
fun rememberIsCurrentlyOpen(openingHour: OpeningHour, dayOfWeekToday: DayOfWeek): Boolean? {
    val isCurrentlyOpen by remember(openingHour, dayOfWeekToday) {
        derivedStateOf { openingHour.isCurrentlyOpen(dayOfWeekToday) }
    }

    return isCurrentlyOpen
}