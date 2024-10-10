package co.ke.xently.features.auth.data

import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.UserEntity
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.data.network.TokenManager
import co.ke.xently.libraries.data.network.UserSessionManager
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserSessionManagerImpl @Inject constructor(
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

    override suspend fun saveSession(user: Map<String, Any?>) {
        database.withTransactionFacade {
            userDao.deleteAll()
            userDao.save(
                UserEntity(
                    id = user["id"] as String,
                    email = user["email"] as String?,
                    emailVerified = user["emailVerified"] as Boolean,
                    name = user["name"] as String?,
                    profilePicUrl = user["profilePicUrl"] as String?,
                    refreshToken = user["refreshToken"] as String?,
                    accessToken = user["accessToken"] as String?,
                )
            )
        }
    }

    companion object {
        private val TAG: String = UserSessionManager::class.java.simpleName
    }
}
