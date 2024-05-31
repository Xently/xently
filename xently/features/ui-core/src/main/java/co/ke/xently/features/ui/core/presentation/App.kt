package co.ke.xently.features.ui.core.presentation

import androidx.compose.runtime.Composable
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.BuildConfig
import co.ke.xently.libraries.ui.image.newImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App(content: @Composable () -> Unit) {
    setSingletonImageLoaderFactory { context ->
        newImageLoader(context, BuildConfig.DEBUG)
    }

    XentlyTheme(content = content)
}