package co.ke.xently.libraries.data.network


interface UserSessionManager: TokenManager {
    suspend fun clearSession()
    suspend fun saveSession(user: Map<String, Any?>)
}