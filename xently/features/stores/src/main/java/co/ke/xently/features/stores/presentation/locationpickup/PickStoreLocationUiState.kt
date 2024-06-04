package co.ke.xently.features.stores.presentation.locationpickup

import androidx.compose.runtime.Stable
import co.ke.xently.libraries.location.tracker.domain.Location

@Stable
data class PickStoreLocationUiState(
    val location: Location? = null,
)