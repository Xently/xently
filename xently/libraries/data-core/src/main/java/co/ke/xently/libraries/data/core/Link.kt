package co.ke.xently.libraries.data.core

import android.os.Parcelable
import io.ktor.http.Url
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

    fun urlWithoutQueryParamTemplates(): Url {
        return Url(hrefWithoutQueryParamTemplates())
    }

    fun hrefWithoutQueryParams(): String {
        return hrefWithoutContentsFrom('?')
    }

    fun urlWithoutQueryParams(): Url {
        return Url(hrefWithoutQueryParams())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun hrefWithoutContentsFrom(delimiter: Char): String {
        return href.replaceAfter(delimiter, "").removeSuffix("$delimiter")
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun urlWithoutContentsFrom(delimiter: Char): Url {
        return Url(hrefWithoutContentsFrom(delimiter))
    }
}
