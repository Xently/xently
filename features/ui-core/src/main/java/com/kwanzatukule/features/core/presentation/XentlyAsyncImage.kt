package com.kwanzatukule.features.core.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import coil3.Extras
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import timber.log.Timber

@Composable
fun XentlyAsyncImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
) {
    // Keep track of the image's memory cache key so isEmpty can be used as a placeholder
    // for the detail screen.
    var placeholder: MemoryCache.Key? = remember(data) { null }
    var hasError by remember(data) { mutableStateOf(false) }
    AnimatedContent(targetState = hasError, label = "xently-async-image") { isError ->
        if (isError) {
            Image(
                modifier = modifier,
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
            )
        } else if (!LocalInspectionMode.current) {
            AsyncImage(
                modifier = modifier,
                contentScale = contentScale,
                contentDescription = contentDescription,
                placeholder = ColorPainter(MaterialTheme.colorScheme.surface),
                onSuccess = {
                    placeholder = it.result.memoryCacheKey
                },
                onError = {
                    hasError = true
                    Timber.tag("XentlyAsyncImage")
                        .e(it.result.throwable, "Failed to load image")
                },
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(data)
                    .placeholderMemoryCacheKey(placeholder)
                    .apply { this.extras.setAll(Extras.EMPTY) }
                    .build(),
            )
        }
    }
}