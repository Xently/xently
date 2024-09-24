package co.ke.xently.libraries.pagination.data

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedResponse<T>(
    @SerialName("_embedded")
    val embedded: Map<String, List<T>> = emptyMap(),
    @SerialName("_links")
    val links: Links = Links(),
    @SerialName("page")
    val page: Page = Page(0, 0, 0, 0),
) {
    @Serializable
    data class Links(
        val prev: Link? = null,
        val next: Link? = null,
        val last: Link? = null,
        val self: Link? = null,
        val first: Link? = null,
    )

    fun get(lookupKey: String? = null): List<T> {
        return getNullable(lookupKey)!!
    }

    fun getNullable(lookupKey: String? = null): List<T>? {
        return embedded[lookupKey ?: DEFAULT_LOOKUP_KEY]
    }

    @Serializable
    data class Page(
        @SerialName("number")
        val number: Int,
        @SerialName("size")
        val size: Int,
        @SerialName("totalElements")
        val totalElements: Int,
        @SerialName("totalPages")
        val totalPages: Int,
    )

    companion object {
        const val DEFAULT_LOOKUP_KEY = "views"
    }
}