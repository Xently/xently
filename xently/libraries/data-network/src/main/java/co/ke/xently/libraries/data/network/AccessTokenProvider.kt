package co.ke.xently.libraries.data.network

import io.ktor.client.HttpClient

interface AccessTokenProvider {
    suspend fun getAccessToken(): String?
    suspend fun getFreshAccessToken(httpClient: HttpClient): String?
}