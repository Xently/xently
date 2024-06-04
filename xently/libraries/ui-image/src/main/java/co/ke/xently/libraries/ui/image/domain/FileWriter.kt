package co.ke.xently.libraries.ui.image.domain

import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

class FileWriter(
    private val directory: File,
    private val fileName: String,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) {
    suspend fun writeBytes(bytes: ByteArray): android.net.Uri {
        return withContext(ioDispatcher) {
            File(directory, fileName).apply {
                writeBytes(bytes)
            }.toUri()
        }
    }
}