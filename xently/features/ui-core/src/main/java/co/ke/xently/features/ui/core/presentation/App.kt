package co.ke.xently.features.ui.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.LocalAuthenticationEventHandler
import co.ke.xently.libraries.ui.core.LocalHttpClient
import co.ke.xently.libraries.ui.core.components.ThemeSetting
import co.ke.xently.libraries.ui.image.newImageLoader
import coil3.compose.setSingletonImageLoaderFactory

@Composable
fun App(
    eventHandler: EventHandler = NoopEventHandler,
    setting: ThemeSetting = rememberSaveable { ThemeSetting.SystemDefault },
    content: @Composable () -> Unit,
) {
    val httpClient = LocalHttpClient.current
    setSingletonImageLoaderFactory { context ->
        newImageLoader(
            context = context,
            debug = context.resources.getBoolean(co.ke.xently.libraries.data.network.R.bool.is_debug),
            httpClient = httpClient,
        )
    }

    CompositionLocalProvider(
        LocalEventHandler provides eventHandler,
        LocalAuthenticationEventHandler provides eventHandler,
    ) {
        XentlyTheme(setting = setting, content = content)
    }
}