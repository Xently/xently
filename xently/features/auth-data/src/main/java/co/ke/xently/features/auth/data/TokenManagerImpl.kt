package co.ke.xently.features.auth.data


import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.libraries.data.network.TokenManager
import io.ktor.client.plugins.auth.providers.BearerTokens
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(database: AuthenticationDatabase) : TokenManager {
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

    companion object {
        private val TAG: String = TokenManager::class.java.simpleName
    }
}
