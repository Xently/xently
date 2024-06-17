package co.ke.xently.features.stores.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.source.ShopRepository
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.StoreFilters
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Error
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.domain.error.ShopSelectionRequiredException
import co.ke.xently.features.stores.data.domain.error.toStoreError
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.stores.data.source.local.StoreEntity
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.String
import kotlin.TODO
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.let
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
) : StoreRepository {
    private val storeDao = database.storeDao()

    override suspend fun save(store: Store): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Flow<Result<Store, DataError>> {
        val store = Store(
            name = "Westlands",
            shop = Shop(name = "Ranalo K'Osewe"),
            description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
        )
        return flowOf(Result.Success(store))
    }

    override fun findActiveStore(): Flow<Result<Store, ConfigurationError>> {
        return storeDao.findActivated()
            .combine(shopRepository.findActivated()) { store, shopResult ->
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
        val urlString = url ?: when (filters.loadType) {
            StoreFilters.LoadType.All -> accessControlRepository.getAccessControl().storesUrl

            StoreFilters.LoadType.ActiveStore -> {
                when (val result = shopRepository.getActivated()) {
                    is ShopResult.Failure -> throw ShopSelectionRequiredException()
                    is ShopResult.Success -> result.data.links["stores"]!!.hrefWithoutQueryParamTemplates()
                }
            }
        }
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
        }.body<PagedResponse<Store>>()
            .let { pagedResponse ->
                val stores = pagedResponse.embedded.values.firstOrNull() ?: emptyList()
                pagedResponse.copy(embedded = mapOf("views" to stores))
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
}