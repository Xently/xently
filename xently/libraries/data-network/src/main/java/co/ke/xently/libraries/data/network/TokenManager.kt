package co.ke.xently.libraries.data.network

import io.ktor.client.plugins.auth.providers.BearerTokens

interface TokenManager {
    suspend fun getTokens(): BearerTokens?
}