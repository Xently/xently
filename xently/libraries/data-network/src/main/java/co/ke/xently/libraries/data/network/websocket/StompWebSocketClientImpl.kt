package co.ke.xently.libraries.data.network.websocket

import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient.Noop.TAG
import io.ktor.client.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
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
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Singleton
class StompWebSocketClientImpl @Inject constructor(
    httpClient: HttpClient,
    private val json: Json,
) : StompWebSocketClient {
    private val wsClient = KtorWebSocketClient(httpClient = httpClient)
    private val stompClient = StompClient(webSocketClient = wsClient) {
        heartBeat = HeartBeat(10.seconds, 10.seconds)
        heartBeatTolerance = HeartBeatTolerance(
            Duration.ZERO,
            10.seconds,
        ) // wide margin to account for heroku cold start
    }
    private val sessions = ConcurrentHashMap<String, StompSessionWithKxSerialization>()

    private suspend fun ensureSessionInitialized(url: String): StompSessionWithKxSerialization {
        return sessions.getOrPut(url) {
            Timber.tag(TAG).d("Initializing session. Connecting to %s", url)
            stompClient.connect(url = url).withJsonConversions(json = json).also {
                Timber.tag(TAG)
                    .d("Initialized session for %s. Total connections: %d", url, sessions.size)
            }
        }
    }

    private suspend fun disconnect(url: String) {
        Timber.tag(TAG).i("Closing session...")
        sessions[url]?.disconnect()
        sessions.remove(url)
        Timber.tag(TAG).i("Closed session")
    }

    override suspend fun sendMessage(
        url: String,
        submissionDelay: Duration,
        send: suspend StompSessionWithKxSerialization.() -> Unit,
    ) {
        delay(submissionDelay)
        Timber.tag(TAG).d("Sending message...")
        try {
            ensureSessionInitialized(url = url).send()
            Timber.tag(TAG).d("Sent message!")
        } catch (ex: Exception) {
            yield()
            Timber.tag(TAG).e(ex, "Error sending message")
        }
    }

    override fun <T : Any> watch(
        url: String,
        maxRetries: MaxRetries,
        initialRetryDelay: Duration,
        shouldRetry: suspend (Throwable) -> Boolean,
        results: suspend StompSessionWithKxSerialization.() -> Flow<T>,
    ) = callbackFlow {
        ensureSessionInitialized(url = url).results().retryWhen { cause, attempt ->
            if (shouldRetry(cause)) {
                val timeMillis =
                    2f.pow(attempt.toInt()).roundToLong() * initialRetryDelay.inWholeMilliseconds
                delay(timeMillis)
                when (maxRetries) {
                    is MaxRetries.Infinite -> true
                    is MaxRetries.Finite -> attempt < maxRetries.retries
                }
            } else false
        }.collect {
            Timber.tag(TAG).d("Received message: %s", it)
            send(it)
        }

        awaitClose {
            launch(NonCancellable) {
                disconnect(url = url)
            }
        }
    }.retryWhen { cause, attempt ->
        if (shouldRetry(cause)) {
            val timeMillis =
                2f.pow(attempt.toInt()).roundToLong() * initialRetryDelay.inWholeMilliseconds
            delay(timeMillis)
            when (maxRetries) {
                is MaxRetries.Infinite -> true
                is MaxRetries.Finite -> attempt < maxRetries.retries
            }
        } else false
    }
}