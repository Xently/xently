package co.ke.xently.features.stores.data.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoreImage(
    val name: String? = null,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    fun url(): String {
        return links["media"]!!.hrefWithoutQueryParamTemplates()
    }
}