package co.ke.xently.libraries.ui.image.domain

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class ImageCompressor(
    private val compressionThresholdInBytes: Long,
    private val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) {
    suspend fun getCompressedByteArray(bytes: ByteArray): ByteArray {
        return withContext(ioDispatcher) {
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            var outputBytes: ByteArray = bytes
            var quality = 100

            do {
                ByteArrayOutputStream().use {
                    bitmap.compress(compressFormat, quality, it)
                    val sizeInMb = outputBytes.size / (1 * 1_024 * 1_024).toDouble()
                    val qualityPercentage = sizeInMb / 10
                    outputBytes = it.toByteArray()
                    quality -= (quality * qualityPercentage.coerceAtMost(0.9)).roundToInt()
                }
            } while (outputBytes.size > compressionThresholdInBytes && quality > 5)

            outputBytes
        }
    }
}