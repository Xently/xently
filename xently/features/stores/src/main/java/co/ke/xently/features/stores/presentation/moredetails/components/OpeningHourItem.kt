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
import co.ke.xently.libraries.ui.core.components.shimmer
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OpeningHourItem(
    openingHour: OpeningHour,
    dateTimeToday: LocalDateTime,
    timePickerState: TimePickerState,
    isLoading: Boolean = false,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val color = getOpeningHourColor(openingHour, dateTimeToday)
        Text(
            color = color,
            modifier = Modifier.shimmer(isLoading),
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
            modifier = Modifier.shimmer(isLoading),
            text = buildString {
                append(openingHour.openTime.toString(timePickerState.is24hour))
                append(" - ")
                append(openingHour.closeTime.toString(timePickerState.is24hour))
            },
        )
    }
}

@Composable
private fun getOpeningHourColor(openingHour: OpeningHour, dateTimeToday: LocalDateTime): Color {
    if (openingHour.dayOfWeek != dateTimeToday.dayOfWeek) return Color.Unspecified

    val isCurrentlyOpen by remember(dateTimeToday, openingHour) {
        derivedStateOf {
            val openTimeMinutes = (openingHour.openTime.hour * 60) + openingHour.openTime.minute
            val closeTimeMinutes = (openingHour.closeTime.hour * 60) + openingHour.closeTime.minute

            val closeDateTime = LocalDateTime(
                dateTimeToday.date,
                LocalTime(openingHour.closeTime.hour, openingHour.closeTime.minute),
            ).run {
                if (openTimeMinutes < closeTimeMinutes) {
                    this
                } else {
                    val timeZone = TimeZone.currentSystemDefault()
                    toInstant(timeZone)
                        .plus(1.days)
                        .toLocalDateTime(timeZone)
                }
            }
            dateTimeToday in LocalDateTime(
                dateTimeToday.date,
                LocalTime(openingHour.openTime.hour, openingHour.openTime.minute),
            )..closeDateTime
        }
    }

    return if (isCurrentlyOpen) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.error
    }
}