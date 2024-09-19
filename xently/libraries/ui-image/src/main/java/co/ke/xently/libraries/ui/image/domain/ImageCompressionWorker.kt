package co.ke.xently.libraries.ui.image.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.data.image.domain.File
import co.ke.xently.libraries.data.image.domain.Progress
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.data.image.exceptions.InvalidFileException
import coil3.toAndroidUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ImageCompressionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dispatchersProvider: DispatchersProvider,
) : CoroutineWorker(appContext, params) {
    /*override suspend fun getForegroundInfo(): ForegroundInfo {
        val channelName =
            applicationContext.getString(R.string.image_compression_notification_channel_name)
        val channel = Channel(
            name = channelName,
            id = "image_compression_notification_channel_name"
        )
        val channelId = DisplayNotification.getOrCreateChannel(
            applicationContext,
            channel = channel,
            channelId = channel.id,
            manifestMetadata = Bundle.EMPTY,
        ) ?: CommonNotificationBuilder.FCM_FALLBACK_NOTIFICATION_CHANNEL
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.image_compression_notification_title))
            .setContentText(applicationContext.getString(R.string.image_compression_notification_body))
            .build()
        return ForegroundInfo(id.hashCode(), notification)
    }*/

    private fun Bitmap.CompressFormat.getFileExtension() = when (this) {
        Bitmap.CompressFormat.JPEG -> "jpeg"
        Bitmap.CompressFormat.PNG -> "png"
        else -> {
            if (name.lowercase().startsWith("webp")) {
                "webp"
            } else {
                throw InvalidFileException()
            }
        }
    }

    override suspend fun doWork(): Result {
        val imageId = params.inputData.getString(EXTRA_INPUT_IMAGE_ID)
            ?: id.toString()
        val uriString = params.inputData.getString(EXTRA_INPUT_IMAGE_URI)
        val compressionThresholdInBytes = params.inputData.getLong(
            EXTRA_INPUT_IMAGE_COMPRESSION_THRESHOLD_BYTES,
            Long.MAX_VALUE
        )
        val ensureCompressedNotLargerThanInBytes = params.inputData.getLong(
            EXTRA_INPUT_IMAGE_ENSURE_COMPRESSED_NOT_LARGER_THAN_BYTES,
            Long.MAX_VALUE
        )
        val compressFormatString = params.inputData.getString(EXTRA_INPUT_IMAGE_COMPRESSION_FORMAT)
            ?: Bitmap.CompressFormat.JPEG.name

        val uri = Uri.parse(uriString)
        val compressFormat = Bitmap.CompressFormat.valueOf(compressFormatString)
        val fileName = "${imageId}.${compressFormat.getFileExtension()}"

        val upload = uri.toCompressedUpload(
            applicationContext,
            fileName = fileName,
            compressionThresholdInBytes = compressionThresholdInBytes,
            ensureCompressedNotLargerThanInBytes = ensureCompressedNotLargerThanInBytes,
            compressFormat = compressFormat,
            dispatchersProvider = dispatchersProvider,
        )

        return when (upload) {
            is File.Error.InvalidFile -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.InvalidFile.name,
                )
            )

            is Progress -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.UnknownResponse.name,
                )
            )

            is UploadResponse -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.UnknownResponse.name,
                )
            )

            is File.Error.FileTooLarge -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.FileTooLarge.name,
                    EXTRA_OUTPUT_FAILURE_FILE_SIZE to upload.fileSize,
                    EXTRA_OUTPUT_FAILURE_EXPECTED_FILE_SIZE to upload.expectedFileSize,
                )
            )

            is UploadRequest -> {
                Result.success(
                    workDataOf(
                        EXTRA_OUTPUT_COMPRESSED_IMAGE_URI to upload.uri.toAndroidUri()
                            .toString(),
                        EXTRA_OUTPUT_IMAGE_SIZE to upload.fileSize,
                        EXTRA_OUTPUT_IMAGE_NAME to (upload.fileName ?: fileName),
                        EXTRA_OUTPUT_IMAGE_MIME_TYPE to (upload.mimeType ?: "image/jpeg"),
                    )
                )
            }
        }
    }

    enum class FailureType {
        UnknownResponse,
        FileTooLarge,
        InvalidFile,
    }

    companion object {
        const val EXTRA_INPUT_IMAGE_ID = "EXTRA_INPUT_IMAGE_ID"
        const val EXTRA_INPUT_IMAGE_URI = "EXTRA_INPUT_IMAGE_URI"
        const val EXTRA_INPUT_IMAGE_COMPRESSION_THRESHOLD_BYTES =
            "EXTRA_INPUT_IMAGE_COMPRESSION_THRESHOLD_BYTES"
        const val EXTRA_INPUT_IMAGE_ENSURE_COMPRESSED_NOT_LARGER_THAN_BYTES =
            "EXTRA_INPUT_IMAGE_ENSURE_COMPRESSED_NOT_LARGER_THAN_BYTES"
        const val EXTRA_INPUT_IMAGE_COMPRESSION_FORMAT = "EXTRA_INPUT_IMAGE_COMPRESSION_FORMAT"
        const val EXTRA_OUTPUT_COMPRESSED_IMAGE_URI = "EXTRA_OUTPUT_COMPRESSED_IMAGE_URI"
        const val EXTRA_OUTPUT_IMAGE_SIZE = "EXTRA_OUTPUT_IMAGE_SIZE"
        const val EXTRA_OUTPUT_IMAGE_NAME = "EXTRA_OUTPUT_IMAGE_NAME"
        const val EXTRA_OUTPUT_IMAGE_MIME_TYPE = "EXTRA_OUTPUT_IMAGE_MIME_TYPE"
        const val EXTRA_OUTPUT_FAILURE_TYPE = "EXTRA_OUTPUT_FAILURE_TYPE"
        const val EXTRA_OUTPUT_FAILURE_FILE_SIZE = "EXTRA_OUTPUT_FAILURE_FILE_SIZE"
        const val EXTRA_OUTPUT_FAILURE_EXPECTED_FILE_SIZE =
            "EXTRA_OUTPUT_FAILURE_EXPECTED_FILE_SIZE"
    }
}