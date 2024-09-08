package co.ke.xently.features.qrcode.data.source

import co.ke.xently.features.qrcode.data.domain.QrCodeResponse
import co.ke.xently.features.qrcode.data.domain.error.Error
import co.ke.xently.features.qrcode.data.domain.error.Result
import co.ke.xently.features.qrcode.data.domain.error.toError
import co.ke.xently.libraries.location.tracker.domain.Location
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.ensureActive
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
internal class QrCodeRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
) : QrCodeRepository {
    override suspend fun getPointsAndReview(
        pointsUrl: String,
        location: Location,
    ): Result<QrCodeResponse, Error> {
        return try {
            val response = httpClient.post(pointsUrl) {
                contentType(ContentType.Application.Json)
                setBody(location)
            }.body<QrCodeResponse>()
            Result.Success(response)
        } catch (ex: Exception) {
            coroutineContext.ensureActive()
            Timber.e(ex)
            Result.Failure(ex.toError())
        }
    }
}