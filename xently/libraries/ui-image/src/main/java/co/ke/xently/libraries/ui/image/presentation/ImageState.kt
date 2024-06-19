package co.ke.xently.libraries.ui.image.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import co.ke.xently.libraries.data.image.domain.Image
import co.ke.xently.libraries.data.image.domain.LoadingProgress
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_INPUT_IMAGE_COMPRESSION_THRESHOLD_BYTES
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_INPUT_IMAGE_ENSURE_COMPRESSED_NOT_LARGER_THAN_BYTES
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_INPUT_IMAGE_ID
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_INPUT_IMAGE_URI
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_COMPRESSED_IMAGE_URI
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_FAILURE_EXPECTED_FILE_SIZE
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_FAILURE_FILE_SIZE
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_FAILURE_TYPE
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_IMAGE_MIME_TYPE
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_IMAGE_NAME
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.Companion.EXTRA_OUTPUT_IMAGE_SIZE
import co.ke.xently.libraries.ui.image.domain.ImageCompressionWorker.FailureType
import coil3.toCoilUri
import java.util.UUID


typealias ImageId = String


@Composable
fun Uri?.imageState(initialValue: Image? = null): State<Image?> {
    return this?.imageState { imageId -> UploadRequest(toCoilUri(), id = imageId) }
        ?: remember(initialValue) { derivedStateOf { initialValue } }
}

@Composable
private inline fun Uri.imageState(initialValue: (ImageId) -> Image): State<Image> {
    val context = LocalContext.current.applicationContext

    val imageId = rememberSaveable(context) { UUID.randomUUID().toString() }

    return produceState(initialValue(imageId), imageId, context) {
        val inputData = workDataOf(
            EXTRA_INPUT_IMAGE_ID to imageId,
            EXTRA_INPUT_IMAGE_URI to this@imageState.toString(),
            EXTRA_INPUT_IMAGE_COMPRESSION_THRESHOLD_BYTES to (500L * 1_024),
            EXTRA_INPUT_IMAGE_ENSURE_COMPRESSED_NOT_LARGER_THAN_BYTES to 5L * 1_024 * 1_024,
        )
        val workRequest = OneTimeWorkRequestBuilder<ImageCompressionWorker>()
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        value = LoadingProgress(id = imageId)

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
                    value = UploadRequest(
                        id = imageId,
                        mimeType = mimeType ?: "image/jpeg",
                        fileName = fileName,
                        fileSize = fileSize,
                        uri = outputUri.toCoilUri(),
                    )
                }

                WorkInfo.State.FAILED -> {
                    val failureTypeString = it.outputData.getString(EXTRA_OUTPUT_FAILURE_TYPE)
                        ?: return@collect
                    when (FailureType.valueOf(failureTypeString)) {
                        FailureType.UnknownResponse -> Unit

                        FailureType.InvalidFile -> {
                            value = Image.Error.InvalidFileError(id = imageId)
                        }

                        FailureType.FileTooLarge -> {
                            value = Image.Error.FileTooLargeError(
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

                WorkInfo.State.RUNNING -> value = LoadingProgress(id = imageId)
                WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED, WorkInfo.State.CANCELLED -> Unit
            }
        }
    }
}
