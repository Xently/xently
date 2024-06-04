package co.ke.xently.libraries.ui.image.domain

import coil3.Uri

interface UriToByteArrayConverter {
    suspend fun convert(uri: Uri): ByteArray?
}