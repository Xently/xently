package co.ke.xently.libraries.data.network.websocket

import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface WebSocketClient {
    suspend fun sendMessage(message: Frame)
    fun listenToSocket(
        url: String,
        maxRetries: Int = 3,
        initialRetryDelay: Duration = 2.seconds,
        shouldRetry: suspend (Throwable) -> Boolean = { true },
    ): Flow<Frame>
}
