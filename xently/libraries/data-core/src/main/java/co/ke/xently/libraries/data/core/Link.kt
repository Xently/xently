package co.ke.xently.libraries.data.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Link(
    val href: String = "",
    val templated: Boolean = false,
    val title: String? = null,
    val name: String? = null,
    val rel: String? = null,
) : Parcelable {
    fun hrefWithoutQueryParamTemplates(): String {
        return hrefWithoutContentsFrom('{')
    }

    fun hrefWithoutQueryParams(): String {
        return hrefWithoutContentsFrom('?')
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun hrefWithoutContentsFrom(delimiter: Char): String {
        return href.replaceAfter(delimiter, "")
            .removeSuffix("$delimiter")
    }
}
