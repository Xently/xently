package com.kwanzatukule.features.authentication.data.di

import co.ke.xently.libraries.data.network.AccessTokenManager
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {
    @Provides
    @Singleton
    fun provideAccessTokenProvider(database: AuthenticationDatabase): AccessTokenManager {
        return object : AccessTokenManager {
            override suspend fun clearUserSession() {
                TODO("Not yet implemented")
            }

            override suspend fun getAccessToken(): String? {
                return database.userDao().first()?.accessToken
            }

            override suspend fun getFreshAccessToken(httpClient: HttpClient): String? {
                return null
            }
        }
    }
}