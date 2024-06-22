package co.ke.xently.features.storecategory.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryDatabase
import co.ke.xently.features.storecategory.data.source.local.StoreCategoryEntity
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
internal class StoreCategoryRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreCategoryDatabase,
    private val accessControlRepository: AccessControlRepository,
) : StoreCategoryRepository {
    private val storeCategoryDao = database.storeCategoryDao()
    private val serverResponseCacheDao = database.serverResponseCacheDao()

    override suspend fun getCategories(url: String?): Flow<List<StoreCategory>> {
        val urlString = url ?: accessControlRepository.getAccessControl().storeCategoriesUrl
        suspend fun save(): List<StoreCategory> {
            val serverResponseCache = serverResponseCacheDao.findById("store_categories")?.takeIf {
                (Clock.System.now() - it.lastUpdated).inWholeMilliseconds < REFRESH_INTERVAL.inWholeMilliseconds
            }
            if (serverResponseCache != null) {
                return storeCategoryDao.getAll()
                    .map { it.storeCategory }
            }

            Timber.tag(TAG).i("Saving store categories response...")
            val categories = httpClient.get(urlString).body<PagedResponse<StoreCategory>>()
                .getNullable(lookupKey = "storeCategoryApiResponses")
                ?: emptyList()
            database.withTransactionFacade {
                storeCategoryDao.deleteAll()
                storeCategoryDao.save(categories.map { StoreCategoryEntity(it) })
                serverResponseCacheDao.save(ServerResponseCache("store_categories"))
            }
            return categories
        }
        return storeCategoryDao.findAll()
            .map { it.map { entity -> entity.storeCategory }.ifEmpty { save() } }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    try {
                        emit(save())
                    } catch (ex: Exception) {
                        if (ex is CancellationException) throw ex
                        Timber.tag(TAG).e(ex, "Failed to refresh store categories")
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
        private val TAG = StoreCategoryRepository::class.java.simpleName
    }
}