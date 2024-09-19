package co.ke.xently.libraries.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

fun interface BaseURL {
    fun get(): String
}

private var cachedBaseURL: String? = null

fun URLBuilder.urlWithSchemaMatchingBaseURL(baseURL: String? = null): URLBuilder {
    val urlString = baseURL ?: cachedBaseURL ?: return this
    val baseURlBuilder = URLBuilder(urlString)
    return apply {
        if (baseURlBuilder.host == host && baseURlBuilder.protocol.name != protocol.name) {
            protocol = baseURlBuilder.protocol
        }
    }
}


class HttpClientFactory private constructor(
    private val json: Json,
    private val accessTokenManager: AccessTokenManager,
    private val baseURL: BaseURL,
) {
    init {
        synchronized(Unit) {
            if (cachedBaseURL == null) {
                Timber.tag(TAG).i("Caching base URL...")
                cachedBaseURL = baseURL.get()
            }
        }
    }

    private val httpClient: HttpClient
        get() = HttpClient(OkHttp) {
            expectSuccess = true
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
                json(json = this@HttpClientFactory.json)
            }
            defaultRequest {
                url(scheme = "https", host = BuildConfig.BASE_HOST)
                contentType(ContentType.Application.Json)
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
        }.withPlugins()

    private fun HttpClient.withPlugins(): HttpClient {
        plugin(HttpSend).intercept { request ->
            request.url {
                takeFrom(urlWithSchemaMatchingBaseURL())
            }
            val authenticationCredentials = request.headers[HttpHeaders.Authorization]

            if (authenticationCredentials == "") {
                // Signals authentication is not required
                request.headers.remove(HttpHeaders.Authorization)
            } else if (authenticationCredentials == null) {
                Timber.tag(TAG)
                    .i("Configuring authentication credentials...")
                val accessToken = accessTokenManager.getAccessToken()
                if (!accessToken.isNullOrBlank()) {
                    Timber.tag(TAG)
                        .i("Successfully configured authentication credentials...")
                    request.headers[HttpHeaders.Authorization] = "Bearer $accessToken"
                }
            }

            val originalCall = execute(request)

            if (originalCall.response.status.value == HttpStatusCode.Unauthorized.value) {
                Timber.tag(TAG)
                    .i("Unauthorized. Attempting to re-authenticate using refresh token...")
                val accessToken = accessTokenManager.getFreshAccessToken(this@withPlugins)
                if (accessToken.isNullOrBlank()) {
                    accessTokenManager.clearUserSession()
                    Timber.tag(TAG)
                        .w("Failed to configure authentication credentials from refresh token")
                    originalCall
                } else {
                    Timber.tag(TAG).i("Successfully configured authentication credentials...")
                    request.headers[HttpHeaders.Authorization] = "Bearer $accessToken"
                    execute(request).also {
                        if (it.response.status.value == HttpStatusCode.Unauthorized.value) {
                            accessTokenManager.clearUserSession()
                            Timber.tag(TAG)
                                .w("Cleared user session. Failed to configure authentication credentials from refresh token")
                        }
                    }
                }
            } else {
                originalCall
            }
        }
        return this
    }

    companion object {
        private const val TAG = "HttpClientFactory"
        operator fun invoke(
            json: Json,
            accessTokenManager: AccessTokenManager,
            baseURL: BaseURL,
        ): HttpClient {
            return HttpClientFactory(
                json = json,
                accessTokenManager = accessTokenManager,
                baseURL = baseURL,
            ).httpClient
        }
    }
}
