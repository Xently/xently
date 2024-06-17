package co.ke.xently.features.stores.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.EmailError
import co.ke.xently.features.stores.data.domain.error.LocationError
import co.ke.xently.features.stores.data.domain.error.NameError
import co.ke.xently.features.stores.data.domain.error.PhoneError
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
    val nameError: NameError? = null,
    val email: String = store.email ?: "",
    val emailError: EmailError? = null,
    val phone: String = store.telephone ?: "",
    val phoneError: PhoneError? = null,
    val description: String = store.description ?: "",
    val location: Location = store.location,
    val locationError: LocationError? = null,
    val locationString: String = location.takeIf(Location::isUsable)
        ?.coordinatesString() ?: "",
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
    }.values.sortedBy { it.dayOfWeek.isoDayNumber },
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val enableSaveButton: Boolean = !isLoading && !disableFields
    val isFormValid: Boolean = nameError == null
            && emailError == null
            && phoneError == null
            && locationError == null
}