package co.ke.xently.libraries.ui.image.domain


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import coil3.toCoilUri
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


fun getPublicPicturesDirectory(): String {
    return Environment.DIRECTORY_PICTURES
}


suspend fun Uri.toCompressedUpload(
    context: Context,
    fileName: String,
    compressionThresholdInBytes: Long = Long.MAX_VALUE,
    ensureCompressedNotLargerThanInBytes: Long = Long.MAX_VALUE,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    ioDispatcher: CoroutineContext = Dispatchers.IO,
): Upload {
    val contentResolver = context.contentResolver

    val reader = FileReader(
        resolver = contentResolver,
        ioDispatcher = ioDispatcher,
    )
    var bytes = reader.readUri(this)
        ?: return Upload.Error.InvalidFileError(id = fileName)

    val compressor = ImageCompressor(
        compressionThresholdInBytes = compressionThresholdInBytes,
        compressFormat = compressFormat,
        ioDispatcher = ioDispatcher,
    )
    bytes = compressor.getCompressedByteArray(bytes)

    if (bytes.size > ensureCompressedNotLargerThanInBytes) {
        return Upload.Error.FileTooLargeError(
            fileSize = bytes.size.toLong(),
            expectedFileSize = ensureCompressedNotLargerThanInBytes,
            id = fileName,
        )
    }

    val writer = FileWriter(
        directory = context.cacheDir,
        fileName = fileName,
        ioDispatcher = ioDispatcher,
    )
    val uri = writer.writeBytes(bytes)

    return Upload.Request(
        mimeType = contentResolver.getType(this),
        fileName = fileName,
        fileSize = bytes.size.toLong(),
        uri = uri.toCoilUri(),
        id = fileName,
    )
}


