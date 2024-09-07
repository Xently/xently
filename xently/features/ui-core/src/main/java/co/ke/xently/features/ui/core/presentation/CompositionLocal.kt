package co.ke.xently.features.ui.core.presentation

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import co.ke.xently.libraries.ui.core.AuthenticationEventHandler

@Immutable
interface EventHandler : AuthenticationEventHandler {
    fun requestShopSelection()
    fun requestStoreSelection(shop: Any? = null)
}

internal val NoopEventHandler = object : EventHandler {
    override fun requestAuthentication() {}
    override fun requestShopSelection() {}
    override fun requestStoreSelection(shop: Any?) {}
}

val LocalEventHandler = staticCompositionLocalOf {
    NoopEventHandler
}

val LocalScrollToTheTop = compositionLocalOf {
    false
}