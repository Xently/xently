package co.ke.xently.features.location.picker.presentation

import androidx.compose.runtime.Stable
import co.ke.xently.libraries.location.tracker.domain.Location

@Stable
data class PickLocationUiState(
    val location: Location? = null,
)