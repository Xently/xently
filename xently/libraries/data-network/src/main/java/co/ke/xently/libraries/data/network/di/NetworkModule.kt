package co.ke.xently.libraries.data.network.di

import co.ke.xently.libraries.data.network.HttpClientFactory
import co.ke.xently.libraries.data.network.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json(from = DefaultJson) {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        tokenManager: TokenManager,
    ): HttpClient {
        return HttpClientFactory(
            json = json,
            tokenManager = tokenManager,
        )
    }
}