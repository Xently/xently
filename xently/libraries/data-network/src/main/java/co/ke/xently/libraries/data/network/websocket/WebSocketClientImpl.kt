package co.ke.xently.libraries.data.network.websocket

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Duration

@Singleton
internal class WebSocketClientImpl @Inject constructor(
    private val httpClient: HttpClient,
) : WebSocketClient {
    private var session: WebSocketSession? = null

    override suspend fun sendMessage(message: Frame) {
        session?.send(message)
    }

    override fun listenToSocket(
        url: String,
        maxRetries: Int,
        initialRetryDelay: Duration,
        shouldRetry: suspend (Throwable) -> Boolean,
    ): Flow<Frame> {
        return callbackFlow {
            session = httpClient.webSocketSession(urlString = url)

            session?.let { session ->
                session
                    .incoming
                    .consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .collect {
                        send(it)
                    }
            } ?: run {
                session?.close()
                session = null
                close()
            }

            awaitClose {
                launch(NonCancellable) {
                    session?.close()
                    session = null
                }
            }
        }.retryWhen { cause, attempt ->
            if (shouldRetry(cause)) {
                val timeMillis =
                    2f.pow(attempt.toInt()).roundToLong() * initialRetryDelay.inWholeMilliseconds
                delay(timeMillis)
                attempt < maxRetries
            } else false
        }
    }
}