package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryEntity
import co.ke.xently.libraries.data.network.websocket.StompWebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
internal class ProductCategoryRepositoryImpl @Inject constructor(
    private val database: ProductCategoryDatabase,
    private val webSocketClient: StompWebSocketClient,
) : ProductCategoryRepository {
    private val productCategoryDao = database.productCategoryDao()

    override fun getCategories(): Flow<List<ProductCategory>> {
        return webSocketClient.watch {
            val destination = "/topic/main.product.categories"
            Timber.tag(StompWebSocketClient.TAG).d("Subscribing to: $destination")
            subscribe<List<ProductCategory>>(destination = destination)
        }.onEach {
            database.withTransactionFacade {
                productCategoryDao.deleteAll()
                productCategoryDao.save(it.map { ProductCategoryEntity(it) })
            }
        }.onStart {
            // Position after onEach to avoid re-caching cached data
            emit(productCategoryDao.getAll().map { it.productCategory })
        }.map { it.sortedBy { it.name } }
    }
}