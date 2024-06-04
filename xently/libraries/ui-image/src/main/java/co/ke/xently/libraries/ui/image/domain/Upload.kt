package co.ke.xently.libraries.ui.image.domain

import androidx.compose.runtime.Stable
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.ui.image.domain.exceptions.InvalidFileException
import coil3.Uri
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Stable
sealed interface Upload {
    val id: String?
    fun key(vararg args: Any): Any

    @Serializable
    @Stable
    data class Response(
        val name: String? = null,
        @SerialName("_links")
        val links: Map<String, Link> = emptyMap(),
        override val id: String? = null,
    ) : Upload {
        fun url(): String {
            return links["media"]!!.hrefWithoutQueryParamTemplates()
        }

        override fun key(vararg args: Any): Any {
            return buildString {
                append(id)
                append(url())
                append(*args)
            }
        }
    }

    sealed interface Error : Upload {
        data class InvalidFileError(override val id: String? = null) : Error {
            override fun key(vararg args: Any): Any {
                return toString()
            }
        }

        data class FileTooLargeError(
            val fileSize: Long,
            val expectedFileSize: Long,
            override val id: String? = null,
        ) : Error {
            override fun key(vararg args: Any): Any {
                return toString()
            }
        }
    }

    @Stable
    data class Progress(
        val bytesSentTotal: Long = 0,
        val contentLength: Long = 0,
        override val id: String? = null,
    ) : Upload {
        @Suppress("unused")
        val isIndeterminate: Boolean
            get() = bytesSentTotal <= 0 || contentLength <= 0

        fun calculate(): Float {
            return (bytesSentTotal / contentLength).toFloat() * 1
        }

        override fun key(vararg args: Any): Any {
            return buildString {
                append(bytesSentTotal + contentLength)
                append(*args)
            }
        }
    }

    @Stable
    data class Request(
        val uri: Uri,
        val fileSize: Long = -1,
        val mimeType: String? = null,
        val fileName: String? = null,
        override val id: String? = null,
    ) : Upload {
        val canUpload = fileSize > 0

        override fun hashCode(): Int {
            return uri.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Request

            return uri == other.uri
        }

        override fun key(vararg args: Any): Any {
            return buildString {
                append(uri.toString(), *args)
            }
        }

        suspend fun upload(
            client: HttpClient,
            urlString: String,
            converter: UriToByteArrayConverter,
        ): Response {
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
                                    this@Request.mimeType ?: "image/jpeg",
                                )
                                this.append(
                                    HttpHeaders.ContentDisposition,
                                    """filename="${this@Request.fileName ?: "Image upload.jpg"}"""",
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
}