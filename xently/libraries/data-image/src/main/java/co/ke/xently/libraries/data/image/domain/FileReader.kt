package co.ke.xently.libraries.data.image.domain

import android.content.ContentResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.CoroutineContext

class FileReader(
    private val resolver: ContentResolver,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
) {
    suspend fun readUri(uri: android.net.Uri): ByteArray? {
        return withContext(ioDispatcher) {
            resolver.openInputStream(uri)?.use { inputStream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(4_096)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    ensureActive()
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                }

                byteArrayOutputStream.toByteArray()
            }
        }
    }
}