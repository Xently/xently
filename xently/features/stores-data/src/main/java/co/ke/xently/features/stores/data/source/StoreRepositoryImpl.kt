package co.ke.xently.features.stores.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Error
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.toStoreError
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.stores.data.source.local.StoreEntity
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.data.image.domain.UriToByteArrayConverter
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.statement.discardRemaining
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.String
import kotlin.TODO
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.run
import kotlin.time.Duration.Companion.milliseconds
import kotlin.to
import co.ke.xently.features.shops.data.domain.error.Result as ShopResult

@Singleton
internal class StoreRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreDatabase,
    private val accessControlRepository: AccessControlRepository,
    private val shopRepository: ShopRepository,
    private val converter: UriToByteArrayConverter,
) : StoreRepository {
    private val storeDao = database.storeDao()

    override suspend fun save(store: Store): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Flow<Result<Store, DataError>> {
        return storeDao.findById(id = id).map { entity ->
            if (entity == null) {
                Result.Failure(DataError.Network.ResourceNotFound)
            } else {
                Result.Success(entity.store)
            }
        }
    }

    override suspend fun getActiveStore(): Result<Store, ConfigurationError> {
        val shop = storeDao.getActivated()
            ?: return Result.Failure(ConfigurationError.StoreSelectionRequired)
        return Result.Success(data = shop.store.copy(isActivated = true))
    }

    override fun findActiveStore(): Flow<Result<Store, ConfigurationError>> {
        return storeDao.findActivated()
            .combine(shopRepository.findActivatedShop()) { store, shopResult ->
                when (store) {
                    null -> {
                        val error = when (shopResult) {
                            is ShopResult.Failure -> ConfigurationError.ShopSelectionRequired
                            is ShopResult.Success -> ConfigurationError.StoreSelectionRequired
                        }
                        Result.Failure(error)
                    }

                    else -> Result.Success(store.store)
                }
            }
    }

    override suspend fun getStores(url: String?, filters: StoreFilters): PagedResponse<Store> {
        val urlString = url ?: accessControlRepository.getAccessControl().storesUrl
        return httpClient.get(urlString = urlString) {
            url {
                parameters.run {
                    if (!filters.query.isNullOrBlank()) set("q", filters.query)
                    if (filters.location != null) {
                        set("latitude", filters.location.latitude.toString())
                        set("longitude", filters.location.longitude.toString())
                    }
                    if (!filters.minimumPrice.isNullOrBlank()) set("minPrice", filters.minimumPrice)
                    if (!filters.maximumPrice.isNullOrBlank()) set("maxPrice", filters.maximumPrice)
                    if (filters.storeCategories.isNotEmpty()) {
                        appendMissing("storeCategory", filters.storeCategories.map { it.name })
                    }
                    appendMissing(
                        "sort",
                        filters.sortBy.ifEmpty {
                            buildList {
                                add("score,desc")
                                if (filters.location != null) {
                                    add("distance,asc")
                                }
                            }
                        },
                    )
                    if (filters.productCategories.isNotEmpty()) {
                        appendMissing(
                            "productCategory",
                            filters.productCategories.map { it.name },
                        )
                    }
                }
            }
        }.body<PagedResponse<Store>>().run {
            copy(embedded = mapOf("views" to (embedded.values.firstOrNull() ?: emptyList())))
        }
    }

    override suspend fun deleteStore(store: Store): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toStoreError())
        }
    }

    override suspend fun selectStore(store: Store): Result<Unit, DataError.Local> {
        database.withTransactionFacade {
            storeDao.deactivateAll()
            storeDao.save(StoreEntity(store = store, isActivated = true))
        }
        return Result.Success(Unit)
    }

    override suspend fun uploadNewImage(
        uploadUrl: String,
        newImage: UploadRequest,
    ): Result<Unit, Error> {
        return try {
            newImage.post(
                client = httpClient,
                converter = converter,
                urlString = uploadUrl,
            )
            updateActiveStoreWithUpdatedImages()
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toStoreError())
        }
    }

    override suspend fun updateImage(
        oldImage: UploadResponse,
        newImage: UploadRequest,
    ): Result<Unit, Error> {
        return try {
            newImage.put(
                client = httpClient,
                converter = converter,
                urlString = oldImage.links["self"]!!.href,
            )
            updateActiveStoreWithUpdatedImages()
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toStoreError())
        }
    }

    override suspend fun removeImage(image: UploadResponse): Result<Unit, Error> {
        return try {
            httpClient.delete(urlString = image.links["self"]!!.href)
                .discardRemaining()
            updateActiveStoreWithUpdatedImages()
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toStoreError())
        }
    }

    private suspend fun updateActiveStoreWithUpdatedImages() {
        when (val result = getActiveStore()) {
            is Result.Failure -> Unit
            is Result.Success -> {
                val store = result.data
                withContext(NonCancellable) {
                    httpClient.get(urlString = store.links["images"]!!.href)
                        .body<PagedResponse<UploadResponse>>().run {
                            val newImages = (embedded.values.firstOrNull() ?: emptyList())
                            selectStore(store.copy(images = newImages))
                        }
                }
            }
        }
    }
}