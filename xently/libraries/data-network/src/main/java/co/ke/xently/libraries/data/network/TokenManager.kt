package co.ke.xently.libraries.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.HttpRequestBuilder

interface TokenManager {
    suspend fun getTokens(): BearerTokens?
    suspend fun getFreshTokens(
        client: HttpClient,
        oldTokens: BearerTokens?,
        block: HttpRequestBuilder.() -> Unit,
    ): BearerTokens?
}