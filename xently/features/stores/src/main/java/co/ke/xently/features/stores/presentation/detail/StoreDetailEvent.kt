package co.ke.xently.features.stores.presentation.detail

import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.features.qrcode.data.domain.error.Error as QrCodeError
import co.ke.xently.features.stores.data.domain.error.Error as StoreError
import co.ke.xently.libraries.location.tracker.domain.error.Error as LocationTrackerError


internal sealed interface StoreDetailEvent {
    data object Success : StoreDetailEvent
    sealed interface Error<out E, out T> : StoreDetailEvent {
        val error: E
        val type: T

        data class Store(
            override val error: UiText,
            override val type: StoreError,
        ) : Error<UiText, StoreError>

        data class QrCode(
            override val error: UiText,
            override val type: QrCodeError,
        ) : Error<UiText, QrCodeError>

        data class LocationTracker(
            override val error: UiText,
            override val type: LocationTrackerError,
        ) : Error<UiText, LocationTrackerError>
    }
}