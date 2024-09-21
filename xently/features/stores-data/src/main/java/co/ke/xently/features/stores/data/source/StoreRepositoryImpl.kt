package co.ke.xently.features.stores.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.openinghours.data.source.OpeningHourRepository
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Error
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.toError
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.stores.data.source.local.StoreEntity
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.data.image.domain.UriToByteArrayConverter
import co.ke.xently.libraries.location.tracker.data.LocationSettingDelegate
import co.ke.xently.libraries.pagination.data.DataManager
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.RemoteMediator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.discardRemaining
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.http.fullPath
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.OptIn
import kotlin.String
import kotlin.Unit
import kotlin.apply
import kotlin.let
import kotlin.random.Random
import kotlin.run
import kotlin.takeIf
import kotlin.time.Duration.Companion.milliseconds
import kotlin.to
import co.ke.xently.features.openinghours.data.domain.error.Result as OpeningHourResult
import co.ke.xently.features.shops.data.domain.error.Result as ShopResult

@Singleton
internal class StoreRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreDatabase,
    private val accessControlRepository: AccessControlRepository,
    private val shopRepository: ShopRepository,
    private val converter: UriToByteArrayConverter,
    private val openingHourRepository: OpeningHourRepository,
    private val dispatchersProvider: DispatchersProvider,
) : StoreRepository {
    private val storeDao = database.storeDao()
    private val currentLocation by LocationSettingDelegate(null)

    override suspend fun save(store: Store, addStoreUrl: String?): Result<Unit, Error> {
        val httpRequestBuilder: HttpRequestBuilder.() -> Unit = {
            contentType(ContentType.Application.Json)
            val body = StoreSaveRequest(
                slug = store.slug,
                name = store.name,
                location = store.location,
                telephone = store.telephone,
                email = store.email,
                description = store.description,
                services = store.services,
                categories = store.categories.toSet(),
            )
            setBody(body)
        }
        try {
            if (store.links["self"]?.href?.isNotBlank() == true) {
                val urlString = store.links["self"]!!.hrefWithoutQueryParamTemplates()
                httpClient.put(urlString, httpRequestBuilder)
            } else {
                val urlString = addStoreUrl ?: when (val result = shopRepository.getActiveShop()) {
                    is ShopResult.Success -> result.data
                    is ShopResult.Failure -> {
                        return Result.Failure(ConfigurationError.valueOf(result.error.name))
                    }
                }.links["stores"]!!.hrefWithoutQueryParamTemplates()
                httpClient.post(urlString, httpRequestBuilder)
            }.body<Store>().let { savedStore ->
                saveOpeningHours(
                    openingHours = store.openingHours,
                    storeOpeningHoursUrl = savedStore.links["opening-hours"]!!.href,
                )

                database.withTransactionFacade {
                    if (storeDao.isActivatedByStoreId(store.id)) {
                        storeDao.save(
                            StoreEntity(
                                isActivated = true,
                                store = savedStore.copy(
                                    openingHours = store.openingHours,
                                    // When saving a store, images are not part of the request data.
                                    // Therefore, we need to reuse the images from the previous store.
                                    images = store.images,
                                ),
                            ),
                        )
                    }
                }
            }
            return Result.Success(Unit)
        } catch (ex: Exception) {
            yield()
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }

    private suspend fun saveOpeningHours(
        storeOpeningHoursUrl: String,
        openingHours: List<OpeningHour>,
    ): List<OpeningHour?> = supervisorScope {
        openingHours.map {
            val selfHref = URLBuilder(storeOpeningHoursUrl)
                .appendPathSegments(it.dayOfWeek.name.lowercase())
                .buildString()
            val hour =
                it.run { copy(links = links.run { copy(self = self.copy(href = selfHref)) }) }
            async {
                when (val result = openingHourRepository.save(hour)) {
                    is OpeningHourResult.Failure -> null
                    is OpeningHourResult.Success -> result.data
                }
            }
        }
    }.awaitAll()

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

    override suspend fun getDefaultStoreFetchUrl(): String {
        return accessControlRepository.getAccessControl().storesUrl
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStores(url: String, filters: StoreFilters): Flow<PagingData<Store>> {
        val pagingConfig = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 0,
        )

        val urlString = URLBuilder(url).apply {
            encodedParameters.run {
                set("size", pagingConfig.pageSize.toString())
                if (!filters.query.isNullOrBlank()) set("q", filters.query)
                val location = filters.location?.takeIf { it.isUsable() } ?: currentLocation
                if (location != null) {
                    set("latitude", location.latitude.toString())
                    set("longitude", location.longitude.toString())
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
                            if (location != null) {
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
        }.build().fullPath
        val keyManager = LookupKeyManager.URL(url = urlString)

        val dataManager = object : DataManager<Store> {
            override suspend fun insertAll(lookupKey: String, data: List<Store>) {
                val activated = storeDao.getActivated()
                storeDao.save(
                    data.map { store ->
                        StoreEntity(
                            store = store,
                            lookupKey = lookupKey,
                            isActivated = store.id == activated?.id,
                        )
                    },
                )
            }

            override suspend fun deleteByLookupKey(lookupKey: String) {
                storeDao.deleteByLookupKey(lookupKey)
            }

            override suspend fun fetchData(url: String?): PagedResponse<Store> {
                return httpClient.get(urlString = url ?: urlString) {
                    headers[HttpHeaders.Authorization] = ""
                }.body<PagedResponse<Store>>()
            }
        }
        val lookupKey = keyManager.getLookupKey()
        return Pager(
            config = pagingConfig,
            remoteMediator = RemoteMediator(
                database = database,
                keyManager = keyManager,
                dataManager = dataManager,
                dispatchersProvider = dispatchersProvider,
            ),
        ) {
            storeDao.getStoresByLookupKey(lookupKey = lookupKey)
        }.flow.map { pagingData ->
            pagingData.map {
                it.store
            }
        }
    }

    override suspend fun deleteStore(store: Store): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            yield()
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }

    override suspend fun selectStore(store: Store): Result<Unit, DataError.Local> {
        database.withTransactionFacade {
            storeDao.deactivateAll()
            storeDao.save(StoreEntity(store = store, isActivated = true))
        }
        return Result.Success(Unit)
    }

    override suspend fun cloneProducts(store: Store): Result<Unit, Error> {
        return try {
            httpClient.post(urlString = store.links["clone-products"]!!.href) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("storeId" to storeDao.getActivated()!!.id))
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            yield()
            Timber.e(ex)
            Result.Failure(ex.toError())
        }
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
            yield()
            Timber.e(ex)
            Result.Failure(ex.toError())
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
            yield()
            Timber.e(ex)
            Result.Failure(ex.toError())
        }
    }

    override suspend fun removeImage(image: UploadResponse): Result<Unit, Error> {
        return try {
            httpClient.delete(urlString = image.links["self"]!!.href)
                .discardRemaining()
            updateActiveStoreWithUpdatedImages()
            Result.Success(Unit)
        } catch (ex: Exception) {
            yield()
            Timber.e(ex)
            Result.Failure(ex.toError())
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
                            storeDao.save(
                                StoreEntity(
                                    store.copy(images = newImages),
                                    isActivated = true,
                                )
                            )
                        }
                }
            }
        }
    }
}