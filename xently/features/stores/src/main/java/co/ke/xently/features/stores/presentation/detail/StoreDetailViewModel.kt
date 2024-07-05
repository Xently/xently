package co.ke.xently.features.stores.presentation.detail

import androidx.lifecycle.SavedStateHandle
import co.ke.xently.features.qrcode.data.source.QrCodeRepository
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.location.tracker.domain.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class StoreDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: StoreRepository,
    qrCodeRepository: QrCodeRepository,
    locationTracker: LocationTracker,
) : AbstractStoreDetailViewModel(
    savedStateHandle = savedStateHandle,
    repository = repository,
    qrCodeRepository = qrCodeRepository,
    locationTracker = locationTracker,
)