package co.ke.xently.features.location.picker.presentation

import co.ke.xently.libraries.location.tracker.domain.Location

internal sealed interface PickLocationAction {
    data object ConfirmSelection : PickLocationAction

    data class UpdateLocation(val location: Location) : PickLocationAction
}