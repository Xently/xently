package co.ke.xently.libraries.data.network

fun interface AccessTokenProvider {
    suspend fun getAccessToken(): String?
}