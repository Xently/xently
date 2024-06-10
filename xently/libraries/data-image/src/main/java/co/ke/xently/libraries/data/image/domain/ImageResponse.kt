package co.ke.xently.libraries.data.image.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val name: String? = null,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
    override val id: String? = null,
) : Image {
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