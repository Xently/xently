package co.ke.xently.libraries.data.network.websocket

import co.ke.xently.libraries.data.network.BuildConfig.BASE_HOST
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val URL = "wss://$BASE_HOST/ws"

interface StompWebSocketClient {
    suspend fun sendMessage(
        url: String = URL,
        submissionDelay: Duration = 100.milliseconds,
        send: suspend StompSessionWithKxSerialization.() -> Unit,
    )
    fun <T: Any> watch(
        url: String = URL,
        maxRetries: MaxRetries = MaxRetries.Infinite,
        initialRetryDelay: Duration = 2.seconds,
        shouldRetry: suspend (Throwable) -> Boolean = { true },
        results: suspend StompSessionWithKxSerialization.() -> Flow<T>,
    ): Flow<T>

    companion object Noop : StompWebSocketClient {
        override suspend fun sendMessage(
            url: String,
            submissionDelay: Duration,
            send: suspend StompSessionWithKxSerialization.() -> Unit,
        ) {
        }

        override fun <T : Any> watch(
            url: String,
            maxRetries: MaxRetries,
            initialRetryDelay: Duration,
            shouldRetry: suspend (Throwable) -> Boolean,
            results: suspend StompSessionWithKxSerialization.() -> Flow<T>,
        ): Flow<T> {
            return flowOf()
        }
    }
}