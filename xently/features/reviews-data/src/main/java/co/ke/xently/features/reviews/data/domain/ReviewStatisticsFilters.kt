package co.ke.xently.features.reviews.data.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ReviewStatisticsFilters(
    val year: Int? = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year,
    val month: Month? = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).month,
)