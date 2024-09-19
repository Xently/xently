package co.ke.xently.features.ui.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.BuildConfig
import co.ke.xently.libraries.ui.core.LocalAuthenticationEventHandler
import co.ke.xently.libraries.ui.core.LocalDispatchersProvider
import co.ke.xently.libraries.ui.core.components.ThemeSetting
import co.ke.xently.libraries.ui.image.newImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App(
    eventHandler: EventHandler = NoopEventHandler,
    setting: ThemeSetting = rememberSaveable { ThemeSetting.SystemDefault },
    content: @Composable () -> Unit,
) {
    val dispatchersProvider = LocalDispatchersProvider.current
    setSingletonImageLoaderFactory { context ->
        newImageLoader(
            context = context,
            debug = BuildConfig.DEBUG,
            dispatchersProvider = dispatchersProvider,
        )
    }

    CompositionLocalProvider(
        LocalEventHandler provides eventHandler,
        LocalAuthenticationEventHandler provides eventHandler,
    ) {
        XentlyTheme(setting = setting, content = content)
    }
}