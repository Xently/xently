package co.ke.xently.features.stores.presentation.locationpickup

import co.ke.xently.libraries.location.tracker.domain.Location

internal sealed interface PickStoreLocationAction {
    data object ConfirmSelection : PickStoreLocationAction

    data class UpdateLocation(val location: Location) : PickStoreLocationAction
}