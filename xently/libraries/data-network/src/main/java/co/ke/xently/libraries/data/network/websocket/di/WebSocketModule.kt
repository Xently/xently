package co.ke.xently.libraries.data.network.websocket.di

import co.ke.xently.libraries.data.network.websocket.StompWebSocketClientConnector
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClientConnectorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class WebSocketModule {
    @Binds
    abstract fun bindStompWebSocketClientConnector(
        client: StompWebSocketClientConnectorImpl,
    ): StompWebSocketClientConnector
}