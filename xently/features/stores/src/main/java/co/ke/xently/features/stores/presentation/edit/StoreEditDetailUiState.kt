package co.ke.xently.features.stores.presentation.edit

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.libraries.location.tracker.domain.Location
import com.dokar.chiptextfield.Chip

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
    val openingHours: List<OpeningHour> = store.openingHours,
    val isLoading: Boolean = false,
    val disableFields: Boolean = false,
) {
    val locationString: String by mutableStateOf(
        location.takeIf(Location::isUsable)
            ?.coordinatesString() ?: ""
    )
}