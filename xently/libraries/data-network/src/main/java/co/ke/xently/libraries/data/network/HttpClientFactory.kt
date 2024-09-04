package co.ke.xently.libraries.data.network

import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
    private val context: Context,
    private val json: Json,
    private val accessTokenProvider: AccessTokenProvider,
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
        get() {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.valueOf(BuildConfig.HTTP_LOG_LEVEL)
                } else {
                    redactHeader(HttpHeaders.Authorization)
                    redactHeader(HttpHeaders.Cookie)
                    HttpLoggingInterceptor.Level.NONE
                }
            }
            val cacheInterceptor = Interceptor { chain ->
                val request = chain.request()
                var response = chain.proceed(
                    request.newBuilder()
                        .cacheControl(CacheControl.parse(request.headers)).build()
                )
                if (response.code == 504 && response.request.cacheControl.onlyIfCached) {
                    // See, https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cache-Control#other
                    response = chain.proceed(
                        response.request.newBuilder()
                            .cacheControl(CacheControl.FORCE_NETWORK).build()
                    )
                }
                return@Interceptor response
            }
            val cache = Cache(context.cacheDir, (5 * 1024 * 1024).toLong())
            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(cacheInterceptor) // maintain order - cache may depend on the headers
                .addInterceptor(loggingInterceptor)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .build()
            return HttpClient(OkHttp) {
                engine {
                    preconfigured = okHttpClient
                }
                configurePlugins()
            }.withPlugins()
        }

    private fun <T : HttpClientEngineConfig> HttpClientConfig<T>.configurePlugins() {
        expectSuccess = true
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        install(HttpRequestRetry) {
            retryIf(maxRetries = 3) { _, response ->
                // Automatically retry server errors
                response.status.value.toString().startsWith("5")
            }
            delayMillis { retry ->
                retry * 3000L
            }
        }
        install(ContentNegotiation) {
            json(json = this@HttpClientFactory.json)
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
            filter { request ->
                request.url.host.contains("ktor.io")
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
    }

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
                val accessToken = accessTokenProvider.getAccessToken()
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
                val accessToken = accessTokenProvider.getFreshAccessToken(this@withPlugins)
                if (accessToken.isNullOrBlank()) {
                    Timber.tag(TAG)
                        .w("Failed to configure authentication credentials from refresh token")
                    originalCall
                } else {
                    Timber.tag(TAG).i("Successfully configured authentication credentials...")
                    request.headers[HttpHeaders.Authorization] = "Bearer $accessToken"
                    execute(request)
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
            context: Context,
            json: Json,
            accessTokenProvider: AccessTokenProvider,
            baseURL: BaseURL,
        ): HttpClient {
            return HttpClientFactory(
                context = context,
                json = json,
                accessTokenProvider = accessTokenProvider,
                baseURL = baseURL,
            ).httpClient
        }
    }
}
