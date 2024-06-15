package co.ke.xently.features.productcategory.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryDatabase
import co.ke.xently.features.productcategory.data.source.local.ProductCategoryEntity
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
    override suspend fun getCategories(url: String?): Flow<List<ProductCategory>> {
        val urlString = url ?: accessControlRepository.getAccessControl().productCategoriesUrl
        suspend fun save(): List<ProductCategory> {
            val categories = httpClient.get(urlString).body<PagedResponse<ProductCategory>>()
                .getNullable(lookupKey = "productCategoryApiResponses")
                ?: emptyList()
            database.withTransactionFacade {
                database.productCategoryDao().deleteAll()
                database.productCategoryDao()
                    .insertAll(categories.map { ProductCategoryEntity(it) })
            }
            return categories
        }
        return database.productCategoryDao().findAll()
            .map { it.map { entity -> entity.productCategory }.ifEmpty { save() } }
            .onEmpty { emit(save()) }
            .onStart {
                val refreshInterval = 10.minutes
                while (true) {
                    try {
                        emit(save())
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        emit(emptyList())
                    }
                    Timber.tag(TAG).i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }
            .catch { emit(emptyList()) }
    }

    companion object {
        private val TAG = ProductCategoryRepository::class.java.simpleName
    }
}