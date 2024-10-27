package co.ke.xently.libraries.data.network.di

import android.content.Context
import co.ke.xently.libraries.data.network.HttpClientFactory
import co.ke.xently.libraries.data.network.R
import co.ke.xently.libraries.data.network.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
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
        @ApplicationContext
        context: Context,
        tokenManager: TokenManager,
    ): HttpClient {
        return HttpClientFactory(
            json = json,
            tokenManager = tokenManager,
            baseHost = context.getString(R.string.base_host),
            isBaseHostSecure = context.resources.getBoolean(R.bool.is_base_host_secure),
            baseHostPort = context.resources.getInteger(R.integer.base_host_port),
            logLevel = if (context.resources.getBoolean(R.bool.is_debug)) {
                LogLevel.ALL
            } else {
                LogLevel.NONE
            },
        )
    }
}