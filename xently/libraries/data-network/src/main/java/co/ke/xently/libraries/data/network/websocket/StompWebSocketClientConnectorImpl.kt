package co.ke.xently.libraries.data.network.websocket

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
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


@Singleton
class StompWebSocketClientConnectorImpl @Inject constructor(
    httpClient: HttpClient,
    private val json: Json,
) : StompWebSocketClientConnector {
    private val wsClient = KtorWebSocketClient(httpClient = httpClient)
    private val stompClient = StompClient(webSocketClient = wsClient) {
        heartBeat = HeartBeat(10.seconds, 10.seconds)
        heartBeatTolerance = HeartBeatTolerance(
            Duration.ZERO,
            10.seconds,
        ) // wide margin to account for heroku cold start
    }
    private val sessions = ConcurrentHashMap<String, StompSessionWithKxSerialization>()

    override suspend fun ensureSessionInitialized(url: String): StompSessionWithKxSerialization {
        return sessions.getOrPut(url) {
            Timber.tag(TAG).d("Initializing session. Connecting to [%s]", url)
            stompClient.connect(url = url).withJsonConversions(json = json)
        }.also {
            Timber.tag(TAG)
                .d("Successfully connected to [%s]. Total connections: %d", url, sessions.size)
        }
    }

    override suspend fun disconnect(url: String) {
        Timber.tag(TAG).i("Closing session...")
        sessions[url]?.disconnect()
        sessions.remove(url)
        Timber.tag(TAG).i("Closed session")
    }

    companion object {
        private val TAG = StompWebSocketClientConnector::class.java.simpleName
    }
}
