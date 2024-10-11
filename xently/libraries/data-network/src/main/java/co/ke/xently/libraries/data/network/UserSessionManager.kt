package co.ke.xently.libraries.data.network

import io.ktor.client.plugins.auth.providers.BearerTokens


interface UserSessionManager: TokenManager {
    suspend fun clearSession()
    suspend fun saveSession(userJson: String): BearerTokens
}