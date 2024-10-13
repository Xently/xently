package co.ke.xently.features.access.control.data

import co.ke.xently.features.access.control.data.local.AccessControlDatabase
import co.ke.xently.features.access.control.data.local.AccessControlEntity
import co.ke.xently.features.access.control.domain.AccessControl
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AccessControlRepositoryImpl @Inject constructor(
    private val database: AccessControlDatabase,
    private val userIdProvider: UserIdProvider,
    private val webSocketClient: StompWebSocketClient,
) : AccessControlRepository() {
    private val accessControlDao = database.accessControlDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun findAccessControl(): Flow<AccessControl> {
        return userIdProvider.currentUserId
            .distinctUntilChanged()
            .flatMapLatest { userId ->
                webSocketClient.watch {
                    val destination = "/queue/permissions.$userId"
                    Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
                    subscribe<AccessControl>(destination = destination)
                }
            }.onEach {
                Timber.tag(TAG).d("Caching received permission...")
                database.withTransactionFacade {
                    accessControlDao
                        .save(AccessControlEntity(it.copyWithDefaultMissingKeys()))
                }
                Timber.tag(TAG).d("Cached received permission")
            }.onStart {
                // Position after onEach to avoid re-caching cached data
                emit(accessControlDao.first()?.accessControl ?: AccessControl())
            }
    }

    companion object {
        private val TAG: String = AccessControlRepository::class.java.simpleName
    }
}