package co.ke.xently.features.openinghours.data.source

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.data.domain.error.Error
import co.ke.xently.features.openinghours.data.domain.error.Result
import co.ke.xently.features.openinghours.data.domain.error.toOpeningHourError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
internal class OpeningHourRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
) : OpeningHourRepository {
    override suspend fun save(hour: OpeningHour): Result<OpeningHour, Error> {
        return try {
            val urlString = hour.links.self.hrefWithoutQueryParams()
            val response = httpClient.post(urlString) {
                contentType(ContentType.Application.Json)
                setBody(hour)
            }.body<OpeningHour>()
            Result.Success(response)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toOpeningHourError())
        }
    }
}