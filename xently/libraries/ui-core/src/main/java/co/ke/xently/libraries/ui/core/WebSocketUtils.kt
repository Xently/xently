package co.ke.xently.libraries.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import kotlinx.coroutines.flow.collectLatest
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe


@Composable
inline fun <reified T : Any> getWebSocketResults(
    initial: T,
    destination: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
): T {
    val client = rememberStompWebSocketClient()
    /*val suggestions by client.watch {
        subscribe<T>(destination = destination)
    }.collectAsStateWithLifecycle(initialValue = initial)*/

    val lifecycle = lifecycleOwner.lifecycle
    val suggestions by produceState(initial, client, lifecycle, minActiveState) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            client.watch {
                subscribe<T>(destination = destination)
            }.collectLatest {
                this@produceState.value = it
            }
        }
    }
    return suggestions
}

@Composable
fun rememberStompWebSocketClient(): StompWebSocketClient {
    val connector = LocalStompWebsocketClientConnector.current
    return remember(connector) {
        StompWebSocketClient(connector = connector)
    }
}