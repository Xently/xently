package co.ke.xently.features.auth.data


import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.UserEntity
import co.ke.xently.libraries.data.network.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    private val database: AuthenticationDatabase,
) : TokenManager {
    private val userDao = database.userDao()
    override suspend fun getTokens(): BearerTokens? {
        Timber.tag(TAG).i("Getting bearer token...")
        val user = userDao.first() ?: return null
        return BearerTokens(
            accessToken = user.accessToken!!,
            refreshToken = user.refreshToken!!,
        ).also {
            Timber.tag(TAG).i("Successfully got bearer token.")
        }
    }

    override suspend fun getFreshTokens(
        client: HttpClient,
        oldTokens: BearerTokens?,
        config: HttpRequestBuilder.() -> Unit,
    ): BearerTokens? {
        val refreshToken = oldTokens?.refreshToken
            ?: getTokens()?.refreshToken
            ?: return null

        Timber.tag(TAG).i("Refreshing bearer tokens...")
        return try {
            val user = client.post(urlString = "/api/v1/auth/refresh") {
                config()
                headers[HttpHeaders.Authorization] = ""
                setBody(mapOf("refreshToken" to refreshToken))
            }.body<UserEntity>()

            Timber.tag(TAG).i("Caching bearer tokens for future use...")
            database.withTransactionFacade {
                userDao.deleteAll()
                userDao.save(user)
                BearerTokens(
                    accessToken = user.accessToken!!,
                    refreshToken = user.refreshToken!!
                )
            }.also { Timber.tag(TAG).i("Successfully refreshed bearer tokens.") }
        } catch (ex: Exception) {
            yield()
            Timber.tag(TAG).e(ex, "Failed to refresh bearer tokens.")
            null
        }.also { bearerTokens ->
            if (bearerTokens == null) {
                Timber.tag(TAG).i("Clearing session...")
                withContext(NonCancellable) {
                    userDao.deleteAll()
                    Timber.tag(TAG).i("Session cleared.")
                }
            }
        }
    }

    companion object {
        private val TAG: String = TokenManager::class.java.simpleName
    }
}
