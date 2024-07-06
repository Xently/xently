package co.ke.xently.libraries.data.image.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadResponse(
    val name: String? = null,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) : Upload {
    fun url(): String {
        return links["media"]!!.hrefWithoutQueryParamTemplates()
    }
}