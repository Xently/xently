package co.ke.xently.libraries.data.network.websocket


import android.content.Context
import co.ke.xently.libraries.data.network.R
import co.ke.xently.libraries.data.network.websocket.utils.NextRetryDelayMilliseconds
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.hildan.krossbow.stomp.ConnectionTimeout
import org.hildan.krossbow.stomp.WebSocketClosedUnexpectedly
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


@Singleton
class StompWebSocketClient @Inject constructor(
    @ApplicationContext
    context: Context,
    private val connector: StompWebSocketClientConnector,
) {
    private val defaultUrl = buildString {
        append(if (context.resources.getBoolean(R.bool.is_base_host_secure)) "wss" else "ws")
        append("://")
        append(context.getString(R.string.base_host))
        append(":")
        append(context.resources.getInteger(R.integer.base_host_port))
        append("/ws")
    }
    suspend fun sendMessage(
        url: String = defaultUrl,
        submissionDelay: Duration = 100.milliseconds,
        send: suspend StompSessionWithKxSerialization.() -> Unit,
    ) {
        delay(submissionDelay)
        Timber.tag(TAG).d("Sending message...")
        try {
            connector.ensureSessionInitialized(url = url)
                .send()
            Timber.tag(TAG).d("Sent message!")
        } catch (ex: Exception) {
            yield()
            Timber.tag(TAG).e(ex, "Error sending message")
            connector.disconnect(url = url)
        }
    }

    fun <T : Any> watch(
        url: String = defaultUrl,
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
    }.catch {
        when (it) {
            is WebSocketClosedUnexpectedly,
            is ConnectionTimeout,
                -> {
                Timber.tag(TAG).d(
                    "[%s] error was encountered. Reconnecting...",
                    it.javaClass.simpleName,
                )
                connector.disconnect(url = url)
            }
        }
        throw it
    }.retryWhen { cause, attempt ->
        if (shouldRetry(cause)) {
            val timeMillis = NextRetryDelayMilliseconds(
                attemptRestart = 5,
                attempt = attempt.toInt(),
                delay = initialRetryDelay,
            )
            Timber.tag(TAG).d(
                cause,
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
    }
}