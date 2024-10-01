package co.ke.xently.libraries.data.network.websocket

import co.ke.xently.libraries.data.network.BuildConfig.BASE_HOST
import kotlinx.coroutines.flow.Flow
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val URL = "wss://$BASE_HOST/ws"

interface StompWebSocketClient {
    suspend fun sendMessage(url: String = URL, send: suspend StompSessionWithKxSerialization.() -> Unit)
    fun <T: Any> watch(
        url: String = URL,
        maxRetries: Int = 3,
        initialRetryDelay: Duration = 2.seconds,
        shouldRetry: suspend (Throwable) -> Boolean = { true },
        results: suspend StompSessionWithKxSerialization.() -> Flow<T>,
    ): Flow<T>
}