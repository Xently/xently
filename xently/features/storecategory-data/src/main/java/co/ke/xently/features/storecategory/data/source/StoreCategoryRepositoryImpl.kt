package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryDatabase
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryEntity
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StoreCategoryRepositoryImpl @Inject constructor(
    private val database: StoreCategoryDatabase,
    private val webSocketClient: StompWebSocketClient,
) : StoreCategoryRepository {
    private val storeCategoryDao = database.storeCategoryDao()

    override fun getCategories(): Flow<List<StoreCategory>> {
        return webSocketClient.watch {
            val destination = "/topic/main.store.categories"
            Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
            subscribe<List<StoreCategory>>(destination = destination)
        }.onEach {
            database.withTransactionFacade {
                storeCategoryDao.deleteAll()
                storeCategoryDao.save(it.map { StoreCategoryEntity(it) })
            }
        }.onStart {
            // Position after onEach to avoid re-caching cached data
            emit(storeCategoryDao.getAll().map { it.storeCategory })
        }.map { it.sortedBy { it.name } }
    }
}