package co.ke.xently.libraries.data.network

import co.ke.xently.libraries.data.network.websocket.utils.NextRetryDelayMilliseconds
import co.ke.xently.libraries.data.network.websocket.utils.NextRetryDelayMilliseconds.ExponentialBackoff.invoke
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds


object HttpClientFactory {
    operator fun invoke(json: Json, tokenManager: TokenManager): HttpClient {
        return HttpClient(OkHttp) {
            expectSuccess = true
            // Refer to - https://ktor.io/docs/client-websockets.html#configure_plugin
            engine {
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(20, TimeUnit.SECONDS)
                    .build()
            }
            defaultRequest {
                url(scheme = "https", host = BuildConfig.BASE_HOST)
                contentType(ContentType.Application.Json)
            }
            install(WebSockets) {
                pingInterval = 20_000
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            install(HttpRequestRetry) {
                retryIf(maxRetries = 3) { _, response ->
                    response.status == HttpStatusCode.RequestTimeout
                            || response.status.value in 500..599
                }
                delayMillis { retry ->
                    NextRetryDelayMilliseconds(
                        attempt = retry,
                        delay = 3.seconds,
                        attemptRestart = 10,
                    )
                }
            }
            install(ContentNegotiation) {
                json(json = json)
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = if (BuildConfig.DEBUG) {
                    LogLevel.ALL
                } else {
                    LogLevel.NONE
                }
                sanitizeHeader { header ->
                    header == HttpHeaders.Authorization
                }
            }
            install(HttpTimeout) {
                val timeout = 30.seconds.inWholeMilliseconds
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenManager.getTokens()
                    }
                    refreshTokens {
                        tokenManager.getFreshTokens(client = client, oldTokens = oldTokens) {
                            markAsRefreshTokenRequest()
                        }
                    }
                    sendWithoutRequest { request ->
                        request.headers[HttpHeaders.Authorization] != ""
                    }
                }
            }
        }
    }
}
