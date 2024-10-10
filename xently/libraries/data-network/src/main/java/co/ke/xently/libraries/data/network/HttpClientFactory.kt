package co.ke.xently.libraries.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.yield
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds


object HttpClientFactory {
    operator fun invoke(json: Json, sessionManager: UserSessionManager): HttpClient {
        return HttpClient(OkHttp) {
            expectSuccess = true
            // Refer to - https://ktor.io/docs/client-websockets.html#configure_plugin
            engine {
                preconfigured = OkHttpClient.Builder()
                    .pingInterval(20, TimeUnit.SECONDS)
                    .build()
            }
            defaultRequest {
                url(scheme = DEFAULT_SCHEME, host = BuildConfig.BASE_HOST)
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
                    (3.seconds * retry).inWholeMilliseconds
                }
            }
            install(ContentNegotiation) {
                json(json = json)
            }
            install(Logging) {
                logger = Logger.ANDROID
                level = if (BuildConfig.DEBUG) {
                    LogLevel.INFO
                } else {
                    LogLevel.NONE
                }
                sanitizeHeader { header ->
                    header == HttpHeaders.Authorization
                }
            }
            install(HttpTimeout) {
                val timeout = 30000L
                connectTimeoutMillis = timeout
                requestTimeoutMillis = timeout
                socketTimeoutMillis = timeout
            }
            install(Auth) {
                bearer {
                    val refreshTokenPath = "/api/v1/auth/refresh"
                    loadTokens {
                        sessionManager.getTokens()
                    }
                    refreshTokens {
                        Timber.tag(TAG).i("Refreshing bearer tokens...")
                        val refreshToken = oldTokens?.refreshToken
                            ?: sessionManager.getTokens()?.refreshToken
                        val bearerTokens = try {
                            client.post(urlString = refreshTokenPath) {
                                setBody(mapOf("refreshToken" to refreshToken))
                                markAsRefreshTokenRequest()
                            }.body<Map<String, Any?>>()
                                .getBearerTokens(sessionManager)
                        } catch (ex: Exception) {
                            yield()
                            Timber.tag(TAG).e(ex, "Failed to refresh bearer tokens.")
                            null
                        }
                        if (bearerTokens == null) {
                            sessionManager.clearSession()
                        }
                        bearerTokens
                    }
                    sendWithoutRequest { request ->
                        request.url.encodedPath == refreshTokenPath
                    }
                }
            }
        }
    }

    private suspend fun Map<String, Any?>.getBearerTokens(sessionManager: UserSessionManager): BearerTokens {
        Timber.tag(TAG)
            .i("Caching bearer tokens for future use...")
        sessionManager.saveSession(this)
        return BearerTokens(
            accessToken = (this["accessToken"] as String),
            refreshToken = (this["refreshToken"] as String),
        ).also {
            Timber.tag(TAG).i("Successfully refreshed bearer tokens.")
        }
    }

    private const val DEFAULT_SCHEME = "https"
    private val TAG = HttpClientFactory::class.java.simpleName
}
