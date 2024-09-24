package co.ke.xently.libraries.ui.image.domain

import androidx.core.net.toUri
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import java.io.File

internal class FileWriter(
    private val directory: File,
    private val fileName: String,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend fun writeBytes(bytes: ByteArray): android.net.Uri {
        return withContext(dispatchersProvider.io) {
            File(directory, fileName).apply {
                writeBytes(bytes)
            }.toUri()
        }
    }
}