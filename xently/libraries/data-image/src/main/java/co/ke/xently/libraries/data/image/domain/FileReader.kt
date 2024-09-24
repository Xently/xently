package co.ke.xently.libraries.data.image.domain

import android.content.ContentResolver
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.ByteArrayOutputStream

class FileReader(
    private val resolver: ContentResolver,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend fun readUri(uri: android.net.Uri): ByteArray? {
        return withContext(dispatchersProvider.io) {
            resolver.openInputStream(uri)?.use { inputStream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(4_096)
                var bytesRead: Int

                yield()
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    yield()
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                }

                byteArrayOutputStream.toByteArray()
            }
        }
    }
}