package co.ke.xently.libraries.ui.core

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import co.ke.xently.libraries.data.auth.AuthenticationState
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

@Immutable
fun interface AuthenticationEventHandler {
    fun requestAuthentication()
}

internal val NoopAuthenticationEventHandler = AuthenticationEventHandler { }

val LocalAuthenticationEventHandler = staticCompositionLocalOf {
    NoopAuthenticationEventHandler
}
val LocalDispatchersProvider = staticCompositionLocalOf<DispatchersProvider> {
    DispatchersProvider.Default
}
val LocalStompWebsocketClient = staticCompositionLocalOf<StompWebSocketClient> {
    StompWebSocketClient.Noop
}
val LocalHttpClient = staticCompositionLocalOf<HttpClient> {
    HttpClient()
}
val LocalJson = staticCompositionLocalOf<Json> {
    Json {
        ignoreUnknownKeys = true
    }
}

val LocalAuthenticationState = compositionLocalOf<State<AuthenticationState>> {
    mutableStateOf(AuthenticationState())
}