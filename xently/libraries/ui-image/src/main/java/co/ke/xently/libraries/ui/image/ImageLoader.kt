package co.ke.xently.libraries.ui.image

import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.data.network.BuildConfig
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.ktor.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import okio.FileSystem

private val HTTP_CLIENT = HttpClient {
    defaultRequest {
        url(scheme = "https", host = BuildConfig.BASE_HOST)
        contentType(ContentType.Application.Json)
    }
    install(Logging) {
        logger = Logger.ANDROID
        level = if (BuildConfig.DEBUG) {
            LogLevel.INFO
        } else {
            LogLevel.NONE
        }
        sanitizeHeader { header ->
            header == HttpHeaders.Authorization
        }
    }
}

fun newImageLoader(
    context: PlatformContext,
    debug: Boolean,
    dispatchersProvider: DispatchersProvider,
): ImageLoader {
    return ImageLoader.Builder(context)
        .coroutineContext(dispatchersProvider.io)
        .components {
            add(SvgDecoder.Factory())
            add(
                KtorNetworkFetcherFactory {
                    HTTP_CLIENT
                },
            )
        }
        .memoryCache {
            MemoryCache.Builder()
                // Set the max size to 25% of the app's available memory.
                .maxSizePercent(context, percent = 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "image_cache")
                .maxSizeBytes(512L * 1024 * 1024) // 512MB
                .build()
        }
        // Show a short crossfade when loading images asynchronously.
        .crossfade(true)
        // Enable logging if this is a debug build.
        .apply {
            if (debug) {
                logger(DebugLogger())
            }
        }
        .build()
}
