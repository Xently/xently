package co.ke.xently.libraries.data.network.websocket.di

import co.ke.xently.libraries.data.network.TokenManager
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClientConnectorImpl.Companion.TAG
import co.ke.xently.libraries.data.network.websocket.utils.NextRetryDelayMilliseconds
import co.ke.xently.libraries.data.network.websocket.utils.NextRetryDelayMilliseconds.ExponentialBackoff.invoke
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import kotlinx.atomicfu.atomic
import kotlinx.datetime.Clock
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.WebSocketConnectionException
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import org.hildan.krossbow.websocket.reconnection.RetryDelayStrategy
import org.hildan.krossbow.websocket.reconnection.withAutoReconnect
import timber.log.Timber
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
internal object WebSocketModule {
    @Provides
    @Singleton
    fun providesStompClient(client: WebSocketClient): StompClient {
        return StompClient(webSocketClient = client) {
            connectionTimeout = 5.minutes
            connectWithStompCommand = true
            heartBeat = HeartBeat(10.seconds, 10.seconds)
            heartBeatTolerance = HeartBeatTolerance(
                Duration.ZERO,
                10.seconds,
            ) // wide margin to account for cold start
        }
    }

    @Provides
    @Singleton
    fun providesWebSocketClient(
        httpClient: HttpClient,
        tokenManager: TokenManager,
    ): WebSocketClient {
        val lastRefreshTokenRetrieval = atomic(Clock.System.now() - 2.days)
        return KtorWebSocketClient(httpClient = httpClient).withAutoReconnect {
            reconnectWhen { throwable, attempt ->
                Timber.tag(TAG).i("Attempting reconnection for the %d time...", attempt + 1)

                if (throwable is WebSocketConnectionException && throwable.httpStatusCode == HttpStatusCode.Unauthorized.value) {
                    val lastTokenRefresh = lastRefreshTokenRetrieval.value
                    val durationSinceLastRefresh = Clock.System.now() - lastTokenRefresh
                    val acceptableRefreshDuration = 20.minutes

                    if (durationSinceLastRefresh < acceptableRefreshDuration) {
                        Timber.tag(TAG).i(
                            "Skipped token refresh since last refresh (%s) was less than %s ago...",
                            lastTokenRefresh,
                            acceptableRefreshDuration,
                        )
                    } else {
                        Timber.tag(TAG).i("Refreshing tokens for reconnection...")
                        tokenManager.getFreshTokens(client = httpClient)?.also {
                            lastRefreshTokenRetrieval.value = Clock.System.now()
                        }
                    }
                }
                true
            }
            afterReconnect {
                Timber.tag(TAG).i("Successfully connected to %s", it.url)
            }
            delayStrategy = object : RetryDelayStrategy {
                override fun computeDelay(attempt: Int): Duration {
                    return NextRetryDelayMilliseconds(
                        delay = 2.seconds,
                        attemptRestart = 10,
                        attempt = attempt.toInt(),
                    ).milliseconds
                }
            }
        }
    }
}
