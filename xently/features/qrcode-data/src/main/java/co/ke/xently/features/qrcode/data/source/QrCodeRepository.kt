package co.ke.xently.features.qrcode.data.source

import co.ke.xently.features.qrcode.data.domain.QrCodeResponse
import co.ke.xently.features.qrcode.data.domain.error.Error
import co.ke.xently.features.qrcode.data.domain.error.Result
import co.ke.xently.libraries.location.tracker.domain.Location

interface QrCodeRepository {
    suspend fun getPointsAndReview(
        pointsUrl: String,
        location: Location,
    ): Result<QrCodeResponse, Error>
}
