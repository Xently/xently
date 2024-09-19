package co.ke.xently.features.auth.data.source.di

import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.UserEntity
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.data.network.AccessTokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class AccessTokenManagerImpl @Inject constructor(
    private val database: AuthenticationDatabase,
    private val dispatchersProvider: DispatchersProvider,
) : AccessTokenManager {
    private val userDao = database.userDao()
    override suspend fun clearUserSession() {
        withContext(NonCancellable + dispatchersProvider.io) {
            userDao.deleteAll()
        }
    }

    override suspend fun getAccessToken(): String? {
        return userDao.first()?.accessToken
    }

    override suspend fun getFreshAccessToken(httpClient: HttpClient): String? {
        val refreshToken = userDao.first()?.refreshToken
            ?: return null

        return try {
            httpClient.post("/api/v1/auth/refresh") {
                headers[HttpHeaders.Authorization] = ""
                setBody(mapOf("refreshToken" to refreshToken))
                contentType(ContentType.Application.Json)
            }.body<UserEntity>().run {
                database.withTransactionFacade {
                    withContext(NonCancellable + dispatchersProvider.io) {
                        userDao.deleteAll()
                        userDao.save(this@run)
                    }
                }
                accessToken
            }
        } catch (ex: Exception) {
            coroutineContext.ensureActive()
            null
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AccessTokenManagerModule {
    @Binds
    abstract fun bindAccessTokenManager(manager: AccessTokenManagerImpl): AccessTokenManager
}
