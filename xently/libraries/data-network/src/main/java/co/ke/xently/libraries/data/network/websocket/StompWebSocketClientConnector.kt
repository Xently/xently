package co.ke.xently.libraries.data.network.websocket

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.frame.StompFrame
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.headers.StompSubscribeHeaders

interface StompWebSocketClientConnector {
    suspend fun ensureSessionInitialized(url: String): StompSessionWithKxSerialization
    suspend fun disconnect(url: String)

    companion object Noop : StompWebSocketClientConnector {
        override suspend fun disconnect(url: String) {}

        override suspend fun ensureSessionInitialized(url: String) = object : StompSession {
            override suspend fun send(
                headers: StompSendHeaders,
                body: FrameBody?,
            ) = null

            override suspend fun subscribe(headers: StompSubscribeHeaders) =
                emptyFlow<StompFrame.Message>()

            override suspend fun ack(ackId: String, transactionId: String?) {}

            override suspend fun nack(ackId: String, transactionId: String?) {}

            override suspend fun begin(transactionId: String) {}

            override suspend fun commit(transactionId: String) {}

            override suspend fun abort(transactionId: String) {}

            override suspend fun disconnect() {}
        }.withJsonConversions(json = Json.Default)
    }
}
