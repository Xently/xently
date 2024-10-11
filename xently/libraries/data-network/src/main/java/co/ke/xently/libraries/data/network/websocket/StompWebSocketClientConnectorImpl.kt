package co.ke.xently.libraries.data.network.websocket

import co.ke.xently.libraries.data.network.TokenManager
import co.ke.xently.libraries.data.network.websocket.utils.NextRetryDelayMilliseconds
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.http.HttpHeaders
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.ConnectionTimeout
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTimedValue


@Singleton
class StompWebSocketClientConnectorImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    private val tokenManager: TokenManager,
) : StompWebSocketClientConnector {
    private val wsClient = KtorWebSocketClient(httpClient = httpClient)
    private val stompClient = StompClient(webSocketClient = wsClient) {
        heartBeat = HeartBeat(10.seconds, 10.seconds)
        heartBeatTolerance = HeartBeatTolerance(
            Duration.ZERO,
            10.seconds,
        ) // wide margin to account for cold start
    }
    private val sessions = ConcurrentHashMap<String, StompSessionWithKxSerialization>()
    private val reconnectionAttempts = atomic(0)

    private suspend fun connect(url: String, bearerTokens: BearerTokens? = null): StompSession {
        Timber.tag(TAG).d("Initializing session. Connecting to [%s]", url)
        return try {
            stompClient.connect(
                url = url,
                customStompConnectHeaders = buildMap {
                    val accessToken = bearerTokens?.accessToken
                    if (!accessToken.isNullOrBlank()) {
                        put(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                },
            )
        } catch (ex: ConnectionTimeout) {
            val attempt = reconnectionAttempts.incrementAndGet()
            val delay = NextRetryDelayMilliseconds(
                attempt = attempt,
                delay = 3.seconds,
                attemptRestart = 10,
            )

            val (tokens, duration) = measureTimedValue {
                delay(delay)
                tokenManager.getFreshTokens(client = httpClient, oldTokens = bearerTokens)
                    ?: throw ex
            }

            Timber.tag(TAG).i(
                "Failed to connect to [%s]. Retrying for the %d time with fresh token after %s...",
                url,
                attempt,
                duration,
            )
            connect(url = url, bearerTokens = tokens)
        }
    }

    override suspend fun ensureSessionInitialized(url: String): StompSessionWithKxSerialization {
        return sessions.getOrPut(url) {
            val tokens = tokenManager.getTokens()
            connect(url = url, bearerTokens = tokens).withJsonConversions(json = json).also {
                Timber.tag(TAG)
                    .d("Connected to [%s] for the first time.", url)
            }
        }
    }

    override suspend fun disconnect(url: String) {
        try {
            sessions[url]?.run {
                Timber.tag(TAG).i("Closing session...")
                disconnect()
            }
        } finally {
            sessions.remove(url)?.also {
                Timber.tag(TAG).i("Closed session")
            }
        }
    }

    companion object {
        private val TAG = StompWebSocketClientConnector::class.java.simpleName
    }
}
