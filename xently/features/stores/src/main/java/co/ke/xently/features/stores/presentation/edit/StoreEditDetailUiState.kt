package co.ke.xently.features.stores.presentation.edit

import androidx.compose.runtime.Stable
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.LocalFieldError
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.location.tracker.domain.Location
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

@Stable
data class StoreEditDetailUiState(
    val categoryName: String = "",
    val store: Store = Store(),
    val name: String = store.name,
    val nameError: List<LocalFieldError>? = null,
    val email: String = store.email ?: "",
    val emailError: List<LocalFieldError>? = null,
    val phone: String = store.telephone ?: "",
    val phoneError: List<LocalFieldError>? = null,
    val description: String = store.description ?: "",
    val descriptionError: List<LocalFieldError>? = null,
    val location: Location = store.location,
    val locationError: List<LocalFieldError>? = null,
    val services: List<StoreService> = store.services,
    val locationString: String = location.takeIf(Location::isUsable)
        ?.coordinatesString() ?: "",
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
    val isFormValid: Boolean = nameError.isNullOrEmpty()
            && emailError.isNullOrEmpty()
            && phoneError.isNullOrEmpty()
            && locationError.isNullOrEmpty()
            && descriptionError.isNullOrEmpty()
}