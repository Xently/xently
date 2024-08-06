package co.ke.xently.features.auth.data.source.di

import co.ke.xently.features.access.control.BuildConfig.BASE_URL
import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.UserEntity
import co.ke.xently.libraries.data.network.AccessTokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Module
@InstallIn(SingletonComponent::class)
object AccessTokenProviderModule {
    @Provides
    @Singleton
    fun provideAccessTokenProvider(database: AuthenticationDatabase): AccessTokenProvider {
        return object : AccessTokenProvider {
            override suspend fun getAccessToken(): String? {
                return database.userDao().first()?.accessToken
            }

            override suspend fun getFreshAccessToken(httpClient: HttpClient): String? {
                val userDao = database.userDao()
                val refreshToken = userDao.first()?.refreshToken
                    ?: return null

                return try {
                    httpClient.post("$BASE_URL/auth/refresh") {
                        url {
                            parameters.run {
                                set("noauth", "0")
                            }
                        }
                        setBody(mapOf("refreshToken" to refreshToken))
                        contentType(ContentType.Application.Json)
                    }.body<UserEntity>().run {
                        database.withTransactionFacade {
                            withContext(NonCancellable + Dispatchers.IO) {
                                userDao.deleteAll()
                                userDao.save(this@run)
                            }
                        }
                        accessToken
                    }
                } catch (ex: Exception) {
                    if (ex is CancellationException) throw ex
                    null
                }.also { token ->
                    if (token == null) {
                        withContext(NonCancellable + Dispatchers.IO) {
                            userDao.deleteAll()
                        }
                    }
                }
            }
        }
    }
}
