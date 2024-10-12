package co.ke.xently.libraries.data.network.websocket

import io.ktor.util.collections.ConcurrentMap
import kotlinx.atomicfu.atomic
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class StompWebSocketClientConnectorImpl @Inject constructor(
    private val json: Json,
    private val stompClient: StompClient,
) : StompWebSocketClientConnector {
    private val sessions = ConcurrentMap<String, StompSessionWithKxSerialization>()
    private val connectionAttemptCount = atomic(0)

    override suspend fun ensureSessionInitialized(url: String): StompSessionWithKxSerialization {
        return sessions.getOrPut(key = url) {
            val attemptCount = connectionAttemptCount.incrementAndGet()

            Timber.tag(TAG).d("Connecting to [%s]", url)
            stompClient.connect(url = url)
                .withJsonConversions(json = json)
                .also {
                    Timber.tag(TAG)
                        .d("Connected to [%s] after %d attempts.", url, attemptCount)
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
        internal val TAG = StompWebSocketClientConnector::class.java.simpleName
    }
}
