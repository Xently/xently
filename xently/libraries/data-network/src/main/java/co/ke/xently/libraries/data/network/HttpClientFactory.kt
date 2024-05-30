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


class HttpClientFactory(
    private val context: Context,
    private val json: Json,
    private val accessTokenProvider: AccessTokenProvider,
) {
    operator fun invoke(): HttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (!BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
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

    private fun URLBuilder.urlWithSchemaMatchingBaseURL(baseURL: String = ""): URLBuilder {
        val baseURlBuilder = URLBuilder(baseURL)
        return apply {
            if (baseURlBuilder.host == host && baseURlBuilder.protocol.name != protocol.name) {
                protocol = baseURlBuilder.protocol
            }
        }
    }

    private fun HttpClient.withPlugins(): HttpClient {
        plugin(HttpSend).intercept { request ->
            request.url {
                takeFrom(urlWithSchemaMatchingBaseURL())
            }

            val shouldConfigureAuth = !request.url.parameters.contains("noauth")
                    && !request.headers.contains(HttpHeaders.Authorization)

            if (shouldConfigureAuth) {
                Timber.i("Configuring authentication credentials...")
                val accessToken = accessTokenProvider.getAccessToken()
                if (!accessToken.isNullOrBlank()) {
                    Timber.i("Successfully configured authentication credentials...")
                    request.headers[HttpHeaders.Authorization] = "Bearer $accessToken"
                }
            }

            execute(request)
        }
        return this
    }
}
