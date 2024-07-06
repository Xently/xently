package co.ke.xently.features.location.picker.presentation

internal sealed interface PickLocationEvent {
    data object SelectionConfirmed : PickLocationEvent
}