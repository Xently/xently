package co.ke.xently.libraries.ui.image.domain

import android.content.Context
import coil3.Uri
import coil3.toAndroidUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriToByteArrayConverterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UriToByteArrayConverter {
    override suspend fun convert(uri: Uri): ByteArray? {
        val reader = FileReader(context.contentResolver)
        return reader.readUri(uri.toAndroidUri())
    }
}