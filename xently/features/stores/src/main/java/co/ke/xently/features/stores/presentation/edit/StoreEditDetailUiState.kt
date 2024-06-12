package co.ke.xently.features.stores.presentation.edit

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.location.tracker.domain.Location
import com.dokar.chiptextfield.Chip
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

@Stable
data class StoreEditDetailUiState(
    val categoryName: String = "",
    val store: Store = Store(),
    val name: String = store.name,
    val email: String = store.email ?: "",
    val phone: String = store.telephone ?: "",
    val description: String = store.description ?: "",
    val location: Location = store.location,
    @Stable
    val services: List<Chip> = store.services.map { Chip(it.name) },
    @Stable
    val openingHours: List<OpeningHour> = buildMap {
        putAll(store.openingHours.associateBy { it.dayOfWeek })
        DayOfWeek.entries.forEach { dayOfWeek ->
            putIfAbsent(
                dayOfWeek,
                OpeningHour(
                    dayOfWeek = dayOfWeek,
                    openTime = Time(7, 0),
                    closeTime = Time(17, 0),
                    open = dayOfWeek.isoDayNumber !in setOf(6, 7),
                ),
            )
        }
    }.values.toList(),
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val locationString: String by mutableStateOf(
        location.takeIf(Location::isUsable)
            ?.coordinatesString() ?: ""
    )
}