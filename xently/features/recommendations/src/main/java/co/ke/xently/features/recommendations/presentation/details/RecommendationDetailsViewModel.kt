package co.ke.xently.features.recommendations.presentation.details

import androidx.lifecycle.SavedStateHandle
import co.ke.xently.features.qrcode.data.source.QrCodeRepository
import co.ke.xently.features.recommendations.data.domain.RecommendationResponse
import co.ke.xently.features.recommendations.data.domain.error.DataError.Local
import co.ke.xently.features.recommendations.data.domain.error.Result
import co.ke.xently.features.recommendations.data.source.RecommendationRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.AbstractStoreDetailViewModel
import co.ke.xently.libraries.location.tracker.domain.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import co.ke.xently.features.stores.data.domain.error.DataError as StoreDataError
import co.ke.xently.features.stores.data.domain.error.Result as StoreResult

@HiltViewModel
class RecommendationDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    qrCodeRepository: QrCodeRepository,
    locationTracker: LocationTracker,
    repository: RecommendationRepository,
) : AbstractStoreDetailViewModel(
    repository = repository,
    locationTracker = locationTracker,
    savedStateHandle = savedStateHandle,
    qrCodeRepository = qrCodeRepository,
) {
    private val _recommendation = MutableStateFlow<RecommendationResponse?>(null)
    val recommendation: StateFlow<RecommendationResponse?> = _recommendation.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getStoreResultFlow(): Flow<StoreResult<Store, StoreDataError>> {
        return savedStateHandle.getStateFlow("recommendationId", -1L)
            .flatMapLatest((repository as RecommendationRepository)::findRecommendationById)
            .map { result ->
                when (result) {
                    is Result.Failure -> {
                        _recommendation.update { null }
                        StoreResult.Failure(result.error.toStoreError())
                    }

                    is Result.Success -> {
                        _recommendation.update { result.data }
                        StoreResult.Success(result.data.store)
                    }
                }
            }
    }

    private fun Local.toStoreError(): StoreDataError {
        return when (this) {
            Local.DISK_FULL -> StoreDataError.Local.DISK_FULL
            Local.ITEM_NOT_FOUND -> StoreDataError.Network.ResourceNotFound
        }
    }
}
