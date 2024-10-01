package co.ke.xently.libraries.data.network.websocket

import io.ktor.client.HttpClient
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
data class OutputMessage(val from: String, val text: String, val time: String) {
    override fun toString(): String {
        return "[$time] $from: $text"
    }
}

@Serializable
data class Message(
    val from: String,
    val text: String,
)

@Singleton
internal class StompWebSocketClientImpl @Inject constructor(
    httpClient: HttpClient,
    private val json: Json,
) : StompWebSocketClient {
    companion object {
        private const val TAG = "StompWebSocketClient"
    }

    private val wsClient = KtorWebSocketClient(httpClient = httpClient)
    private val stompClient = StompClient(webSocketClient = wsClient) {
        heartBeat = HeartBeat(10.seconds, 10.seconds)
        heartBeatTolerance = HeartBeatTolerance(
            Duration.ZERO,
            10.seconds,
        ) // wide margin to account for heroku cold start
    }
    private val sessionMutex = Mutex()
    private var session = atomic<StompSessionWithKxSerialization?>(null)

    private suspend fun ensureSessionInitialized(url: String): StompSessionWithKxSerialization {
        return sessionMutex.withLock {
            if (session.value == null) {
                Timber.tag(TAG).d("Initializing session. Connecting to %s", url)
                session.value = stompClient.connect(url = url).withJsonConversions(json = json)
            }
            session.value!!
        }
    }

    private suspend fun disconnect() {
        Timber.tag(TAG).i("Closing session")
        sessionMutex.withLock {
            session.value?.disconnect()
            session.value = null
        }
    }

    override suspend fun sendMessage(
        url: String,
        send: suspend StompSessionWithKxSerialization.() -> Unit,
    ) {
        ensureSessionInitialized(url = url).send()
    }

    override fun <T : Any> watch(
        url: String,
        maxRetries: Int,
        initialRetryDelay: Duration,
        shouldRetry: suspend (Throwable) -> Boolean,
        results: suspend StompSessionWithKxSerialization.() -> Flow<T>,
    ) = callbackFlow {
        ensureSessionInitialized(url = url).results().collect {
            Timber.tag(TAG).d("Received message: %s", it)
            send(it)
        }

        awaitClose {
            launch(NonCancellable) {
                disconnect()
            }
        }
    }.retryWhen { cause, attempt ->
        if (shouldRetry(cause)) {
            val timeMillis =
                2f.pow(attempt.toInt())
                    .roundToLong() * initialRetryDelay.inWholeMilliseconds
            delay(timeMillis)
            attempt < maxRetries
        } else false
    }
}