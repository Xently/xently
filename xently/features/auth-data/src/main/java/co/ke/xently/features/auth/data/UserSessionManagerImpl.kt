package co.ke.xently.features.auth.data

import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.UserEntity
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.data.network.TokenManager
import co.ke.xently.libraries.data.network.UserSessionManager
import io.ktor.client.plugins.auth.providers.BearerTokens
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserSessionManagerImpl @Inject constructor(
    private val json: Json,
    private val database: AuthenticationDatabase,
    private val dispatchersProvider: DispatchersProvider,
    private val tokenManager: TokenManager,
) : UserSessionManager, TokenManager by tokenManager {
    private val userDao = database.userDao()
    override suspend fun clearSession() {
        Timber.tag(TAG).i("Clearing session...")
        withContext(NonCancellable + dispatchersProvider.io) {
            userDao.deleteAll()
            Timber.tag(TAG).i("Session cleared.")
        }
    }

    override suspend fun saveSession(userJson: String): BearerTokens {
        return database.withTransactionFacade {
            userDao.deleteAll()
            val user = json.decodeFromString<UserEntity>(userJson)
            userDao.save(user)
            BearerTokens(accessToken = user.accessToken!!, refreshToken = user.refreshToken!!)
        }
    }

    companion object {
        private val TAG: String = UserSessionManager::class.java.simpleName
    }
}
