package com.kwanzatukule.features.core.presentation

import androidx.compose.runtime.Composable
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory
import com.kwanzatukule.features.core.BuildConfig
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme

@OptIn(ExperimentalCoilApi::class)
@Composable
fun App(content: @Composable () -> Unit) {
    setSingletonImageLoaderFactory { context ->
        newImageLoader(context, BuildConfig.DEBUG)
    }

    KwanzaTukuleTheme(
        content = content,
        dynamicColor = false,
    )
}