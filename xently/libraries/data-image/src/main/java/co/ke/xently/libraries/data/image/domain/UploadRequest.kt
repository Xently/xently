package co.ke.xently.libraries.data.image.domain

import co.ke.xently.libraries.data.image.exceptions.InvalidFileException
import coil3.Uri
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

data class UploadRequest(
    val uri: Uri,
    val fileSize: Long = -1,
    val mimeType: String? = null,
    val fileName: String? = null,
) : Upload {
    override fun hashCode(): Int {
        return uri.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UploadRequest

        return uri == other.uri
    }

    suspend fun upload(
        client: HttpClient,
        urlString: String,
        converter: UriToByteArrayConverter,
    ): UploadResponse {
        val bytes = converter.convert(uri) ?: throw InvalidFileException()

        return client.post(urlString) {
            val dataContent = MultiPartFormDataContent(
                formData {
                    this.append(
                        "file",
                        bytes,
                        Headers.build {
                            this.append(
                                HttpHeaders.ContentType,
                                this@UploadRequest.mimeType ?: "image/jpeg",
                            )
                            this.append(
                                HttpHeaders.ContentDisposition,
                                """filename="${this@UploadRequest.fileName ?: "Image upload.jpg"}"""",
                            )
                        },
                    )
                },
            )
            this.setBody(dataContent)
            /*this.onUpload { bytesSentTotal, contentLength ->
                onUploadProgress(UploadProgress(bytesSentTotal, contentLength))
            }*/
        }.body()
    }
}