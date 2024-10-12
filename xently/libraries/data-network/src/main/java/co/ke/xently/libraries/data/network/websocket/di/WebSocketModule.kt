package co.ke.xently.libraries.data.network.websocket.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
internal object WebSocketModule {
    @Provides
    @Singleton
    fun providesStompClient(client: WebSocketClient): StompClient {
        return StompClient(webSocketClient = client) {
            connectionTimeout = 5.minutes
            connectWithStompCommand = true
            heartBeat = HeartBeat(10.seconds, 10.seconds)
            heartBeatTolerance = HeartBeatTolerance(
                Duration.ZERO,
                10.seconds,
            ) // wide margin to account for cold start
        }
    }

    @Provides
    @Singleton
    fun providesWebSocketClient(httpClient: HttpClient): WebSocketClient {
        return KtorWebSocketClient(httpClient = httpClient)
    }
}
