package co.ke.xently.libraries.data.image.domain

import android.content.Context
import co.ke.xently.libraries.data.core.DispatchersProvider
import coil3.Uri
import coil3.toAndroidUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriToByteArrayConverterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
) : UriToByteArrayConverter {
    override suspend fun convert(uri: Uri): ByteArray? {
        val reader = FileReader(
            resolver = context.contentResolver,
            dispatchersProvider = dispatchersProvider,
        )
        return reader.readUri(uri.toAndroidUri())
    }
}