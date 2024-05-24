package com.kwanzatukule.di

import android.content.Context
import com.kwanzatukule.AppDatabase
import com.kwanzatukule.features.core.data.HttpClientFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext
        context: Context,
        json: Json,
        database: AppDatabase,
    ): HttpClient {
        return HttpClientFactory(
            context = context,
            json = json,
            accessTokenProvider = { database.userDao().first()?.accessToken },
        )()
    }
}