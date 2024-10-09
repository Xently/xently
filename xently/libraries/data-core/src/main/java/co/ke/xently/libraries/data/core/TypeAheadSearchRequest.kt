package co.ke.xently.libraries.data.core;

import kotlinx.serialization.Serializable

@Serializable
data class TypeAheadSearchRequest(
    val query: String,
    val size: Int = 5,
)
