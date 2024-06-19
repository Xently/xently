package co.ke.xently.libraries.ui.image

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import co.ke.xently.libraries.data.image.domain.LoadingProgress
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.data.network.urlWithSchemaMatchingBaseURL
import coil3.Extras
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import io.ktor.http.URLBuilder
import timber.log.Timber


@Composable
fun XentlyImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    contentDescription: String? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {
    AnimatedContent(data, label = "xently-image") {
        when (it) {
            null, is LoadingProgress -> Unit

            is UploadRequest -> {
                XentlyAsyncImage(
                    data = it.uri,
                    modifier = modifier,
                    contentScale = contentScale,
                    contentDescription = contentDescription,
                    onError = onError,
                )
            }

            is UploadResponse -> {
                val url by remember {
                    derivedStateOf {
                        URLBuilder(it.url()).urlWithSchemaMatchingBaseURL()
                            .buildString()
                    }
                }
                XentlyAsyncImage(
                    data = url,
                    modifier = modifier,
                    contentScale = contentScale,
                    contentDescription = contentDescription,
                    onError = onError,
                )
            }

            else -> {
                XentlyAsyncImage(
                    data = it,
                    modifier = modifier,
                    contentScale = contentScale,
                    contentDescription = contentDescription,
                    onError = onError,
                )
            }
        }
    }
}

@Composable
private fun XentlyAsyncImage(
    data: Any?,
    modifier: Modifier,
    contentScale: ContentScale,
    contentDescription: String?,
    onError: ((AsyncImagePainter.State.Error) -> Unit)?,
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
                    if (onError == null) {
                        hasError = true
                    } else {
                        onError(it)
                    }
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