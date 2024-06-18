package co.ke.xently.features.stores.presentation.locationpickup

internal sealed interface PickStoreLocationEvent {
    data object SelectionConfirmed : PickStoreLocationEvent
}