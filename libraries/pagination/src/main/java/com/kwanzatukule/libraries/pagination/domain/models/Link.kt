package com.kwanzatukule.libraries.pagination.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val href: String = "",
    val templated: Boolean = false,
    val title: String? = null,
    val name: String? = null,
    val rel: String? = null,
) {
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