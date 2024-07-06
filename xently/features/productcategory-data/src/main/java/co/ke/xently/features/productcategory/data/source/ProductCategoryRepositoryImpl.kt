package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryEntity
import co.ke.xently.libraries.data.local.ServerResponseCache
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.minutes

@Singleton
internal class ProductCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProductCategoryDatabase,
    private val accessControlRepository: AccessControlRepository,
) : ProductCategoryRepository {
    private val productCategoryDao = database.productCategoryDao()
    private val serverResponseCacheDao = database.serverResponseCacheDao()

    override suspend fun getCategories(url: String?): Flow<List<ProductCategory>> {
        val urlString = url ?: accessControlRepository.getAccessControl().productCategoriesUrl
        suspend fun save(): List<ProductCategory> {
            val serverResponseCache =
                serverResponseCacheDao.findById("product_categories")?.takeIf {
                    (Clock.System.now() - it.lastUpdated).inWholeMilliseconds < REFRESH_INTERVAL.inWholeMilliseconds
                }
            if (serverResponseCache != null) {
                return productCategoryDao.getAll()
                    .map { it.productCategory }
            }

            Timber.tag(TAG).i("Saving product categories response...")
            val categories = httpClient.get(urlString).body<PagedResponse<ProductCategory>>()
                .getNullable(lookupKey = "productCategoryApiResponses")
                ?: emptyList()
            database.withTransactionFacade {
                productCategoryDao.deleteAll()
                productCategoryDao.save(categories.map { ProductCategoryEntity(it) })
                serverResponseCacheDao.save(ServerResponseCache("product_categories"))
            }
            return categories
        }
        return productCategoryDao.findAll()
            .map { it.map { entity -> entity.productCategory }.ifEmpty { save() } }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    try {
                        emit(save())
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        Timber.tag(TAG).e(ex, "Failed to refresh product categories")
                        emit(emptyList())
                    }
                    Timber.tag(TAG).i("Waiting %s before another check...", REFRESH_INTERVAL)
                    delay(REFRESH_INTERVAL)
                }
            }
            .catch { emit(emptyList()) }
    }

    companion object {
        private val REFRESH_INTERVAL = 30.minutes
        private val TAG = ProductCategoryRepository::class.java.simpleName
    }
}