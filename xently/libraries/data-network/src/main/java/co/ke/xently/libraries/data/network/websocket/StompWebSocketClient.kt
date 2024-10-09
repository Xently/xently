package co.ke.xently.libraries.data.network.websocket


import co.ke.xently.libraries.data.network.BuildConfig.BASE_HOST
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


@Singleton
class StompWebSocketClient @Inject constructor(
    private val connector: StompWebSocketClientConnector,
) {
    suspend fun sendMessage(
        url: String = URL,
        submissionDelay: Duration = 100.milliseconds,
        send: suspend StompSessionWithKxSerialization.() -> Unit,
    ) {
        delay(submissionDelay)
        Timber.tag(TAG).d("Sending message...")
        try {
            connector.ensureSessionInitialized(url = url).send()
            Timber.tag(TAG).d("Sent message!")
        } catch (ex: Exception) {
            yield()
            Timber.tag(TAG).e(ex, "Error sending message")
        }
    }

    fun <T : Any> watch(
        url: String = URL,
        maxRetries: MaxRetries = MaxRetries.Infinite,
        initialRetryDelay: Duration = 2.seconds,
        shouldRetry: suspend (Throwable) -> Boolean = { true },
        results: suspend StompSessionWithKxSerialization.() -> Flow<T>,
    ) = callbackFlow {
        connector.ensureSessionInitialized(url = url).results().collect {
            Timber.tag(TAG).d("Received message: %s", it)
            send(it)
        }

        awaitClose {
            launch(NonCancellable) {
                connector.disconnect(url = url)
            }
        }
    }.retryWhen { cause, attempt ->
        if (shouldRetry(cause)) {
            val timeMillis =
                2f.pow(attempt.toInt()).roundToLong() * initialRetryDelay.inWholeMilliseconds
            Timber.tag(TAG).d(
                "An error '%s' was encountered. Retrying in %s for the %d time...",
                cause.message,
                timeMillis.milliseconds,
                attempt + 1,
            )
            delay(timeMillis)
            when (maxRetries) {
                is MaxRetries.Infinite -> true
                is MaxRetries.Finite -> attempt < maxRetries.retries
            }
        } else false
    }

    companion object {
        const val TAG = "StompWebSocketClient"
        private const val URL = "wss://$BASE_HOST/ws"
    }
}