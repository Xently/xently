package co.ke.xently.libraries.pagination.data

import io.ktor.http.Url
import io.ktor.util.flattenEntries

fun interface LookupKeyManager {
    fun getLookupKey(): String

    companion object Default : LookupKeyManager {
        const val DEFAULT_KEY = "_default_"
        override fun getLookupKey(): String {
            return DEFAULT_KEY
        }
    }

    class URL(
        private val url: Url,
        private val ignoreQueryKeys: Set<String> = DEFAULT_IGNORE_QUERY_KEYS,
    ) : LookupKeyManager {
        companion object {
            private val DEFAULT_IGNORE_QUERY_KEYS = setOf("page")
        }

        constructor(
            url: String,
            ignoreQueryKeys: Set<String> = DEFAULT_IGNORE_QUERY_KEYS,
        ) : this(Url(url), ignoreQueryKeys)

        constructor(
            urlPath: String,
            queryParams: String,
            ignoreQueryKeys: Set<String> = DEFAULT_IGNORE_QUERY_KEYS,
        ) : this("$urlPath?$queryParams", ignoreQueryKeys)

        override fun getLookupKey(): String {
            return url.run {
                parameters.flattenEntries()
                    .filter { (key, _) ->
                        key !in ignoreQueryKeys
                    }.toSortedSet { (key1, value1), (key2, value2) ->
                        key1.compareTo(key2).takeIf { it != 0 }
                            ?: value1.compareTo(value2)
                    }.joinToString(prefix = "$encodedPath?", separator = "&") { (key, value) ->
                        "$key=$value"
                    }
            }
        }
    }
}