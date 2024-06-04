package co.ke.xently.libraries.ui.image.domain

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.ke.xently.libraries.ui.image.domain.exceptions.InvalidFileException
import coil3.toAndroidUri
import coil3.toCoilUri
import java.util.UUID

class ImageCompressionWorker(
    appContext: Context,
    private val params: WorkerParameters,
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
        )

        return when (upload) {
            is Upload.Error.InvalidFileError -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.InvalidFile.name,
                )
            )

            is Upload.Progress -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.UnknownResponse.name,
                )
            )

            is Upload.Response -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.UnknownResponse.name,
                )
            )

            is Upload.Error.FileTooLargeError -> Result.failure(
                workDataOf(
                    EXTRA_OUTPUT_FAILURE_TYPE to FailureType.FileTooLarge.name,
                    EXTRA_OUTPUT_FAILURE_FILE_SIZE to upload.fileSize,
                    EXTRA_OUTPUT_FAILURE_EXPECTED_FILE_SIZE to upload.expectedFileSize,
                )
            )

            is Upload.Request -> {
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

        @Composable
        fun uploadState(uri: Uri): State<Upload> {
            val context = LocalContext.current.applicationContext

            val imageId = remember(uri, context) {
                UUID.randomUUID().toString()
            }

            return produceState<Upload>(
                Upload.Request(uri.toCoilUri(), id = imageId),
                imageId,
                uri,
                context,
            ) {
                val inputData = workDataOf(
                    EXTRA_INPUT_IMAGE_ID to imageId,
                    EXTRA_INPUT_IMAGE_URI to uri.toString(),
                    EXTRA_INPUT_IMAGE_COMPRESSION_THRESHOLD_BYTES to (500L * 1_024),
                    EXTRA_INPUT_IMAGE_ENSURE_COMPRESSED_NOT_LARGER_THAN_BYTES to 5L * 1_024 * 1_024,
                )
                val workRequest = OneTimeWorkRequestBuilder<ImageCompressionWorker>()
                    .setInputData(inputData)
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
                value = Upload.Progress(id = imageId)

                val workManager = WorkManager.getInstance(context)
                workManager.enqueue(workRequest)

                workManager.getWorkInfoByIdFlow(workRequest.id).collect {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val outputUriString =
                                it.outputData.getString(EXTRA_OUTPUT_COMPRESSED_IMAGE_URI)
                            val fileName = it.outputData.getString(EXTRA_OUTPUT_IMAGE_NAME)
                            val mimeType = it.outputData.getString(EXTRA_OUTPUT_IMAGE_MIME_TYPE)
                            val fileSize = it.outputData.getLong(EXTRA_OUTPUT_IMAGE_SIZE, 0)
                            val outputUri = Uri.parse(outputUriString)
                            value = Upload.Request(
                                id = imageId,
                                mimeType = mimeType ?: "image/jpeg",
                                fileName = fileName,
                                fileSize = fileSize,
                                uri = outputUri.toCoilUri(),
                            )
                        }

                        WorkInfo.State.FAILED -> {
                            val failureTypeString =
                                it.outputData.getString(EXTRA_OUTPUT_FAILURE_TYPE)
                                    ?: return@collect
                            when (FailureType.valueOf(failureTypeString)) {
                                FailureType.UnknownResponse -> Unit

                                FailureType.InvalidFile -> {
                                    value = Upload.Error.InvalidFileError(id = imageId)
                                }

                                FailureType.FileTooLarge -> {
                                    value = Upload.Error.FileTooLargeError(
                                        id = imageId,
                                        fileSize = it.outputData.getLong(
                                            EXTRA_OUTPUT_FAILURE_FILE_SIZE,
                                            0L
                                        ),
                                        expectedFileSize = it.outputData.getLong(
                                            EXTRA_OUTPUT_FAILURE_EXPECTED_FILE_SIZE,
                                            0L
                                        ),
                                    )
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}