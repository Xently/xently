package co.ke.xently.features.shops.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopFilters
import co.ke.xently.features.shops.data.domain.error.ConfigurationError
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Error
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.domain.error.toError
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.features.shops.data.source.local.ShopEntity
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.pagination.data.DataManager
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.RemoteMediator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.fullPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Singleton
internal class ShopRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ShopDatabase,
    private val accessControlRepository: AccessControlRepository,
    private val dispatchersProvider: DispatchersProvider,
) : ShopRepository {
    private val shopDao = database.shopDao()

    override suspend fun save(shop: Shop, merchant: Merchant): Result<Unit, Error> {
        try {
            val urlString = accessControlRepository.getAccessControl().addShopUrl
            httpClient.post(urlString) {
                contentType(ContentType.Application.Json)
                setBody(
                    ShopAndMerchantSaveRequest(
                        shop = ShopAndMerchantSaveRequest.Shop(
                            name = shop.name,
                            onlineShopUrl = shop.onlineShopUrl,
                        ),
                        merchant = ShopAndMerchantSaveRequest.Merchant(
                            firstName = merchant.firstName,
                            lastName = merchant.lastName,
                            emailAddress = merchant.emailAddress,
                        ),
                    )
                )
            }.body<Shop>()
            return Result.Success(Unit)
        } catch (ex: Exception) {
            coroutineContext.ensureActive()
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }

    override fun findActivatedShop(): Flow<Result<Shop, ConfigurationError>> {
        return shopDao.findActivated().map {
            when (it) {
                null -> Result.Failure(ConfigurationError.ShopSelectionRequired)
                else -> Result.Success(data = it.shop.copy(isActivated = true))
            }
        }
    }

    override suspend fun getShopsUrlAssociatedWithCurrentUser(): String {
        return accessControlRepository.getAccessControl().shopsAssociatedWithMyAccountUrl
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getShops(url: String, filters: ShopFilters): Flow<PagingData<Shop>> {
        val pagingConfig = PagingConfig(
            pageSize = 20,
//            initialLoadSize = 20,
//            prefetchDistance = 0,
        )

        val urlString = URLBuilder(url).apply {
            encodedParameters.run {
                set("size", pagingConfig.pageSize.toString())
                if (!filters.query.isNullOrBlank()) {
                    set("query", filters.query)
                }
            }
        }.build().fullPath
        val keyManager = LookupKeyManager.URL(url = urlString)

        val dataManager = object : DataManager<Shop> {
            override suspend fun insertAll(lookupKey: String, data: List<Shop>) {
                val activated = shopDao.getActivated()
                shopDao.save(
                    data.map { shop ->
                        ShopEntity(
                            shop = shop,
                            lookupKey = lookupKey,
                            isActivated = shop.id == activated?.id,
                        )
                    },
                )
            }

            override suspend fun deleteByLookupKey(lookupKey: String) {
                shopDao.deleteByLookupKeyExceptActivated(lookupKey)
            }

            override suspend fun fetchData(url: String?): PagedResponse<Shop> {
                return httpClient.get(urlString = url ?: urlString)
                    .body<PagedResponse<Shop>>()
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
            shopDao.getShopsByLookupKey(lookupKey = lookupKey)
        }.flow.map { pagingData ->
            pagingData.map {
                it.shop
            }
        }
    }

    override suspend fun deleteShop(shop: Shop): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            coroutineContext.ensureActive()
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }

    override suspend fun selectShop(shop: Shop): Result<Unit, DataError.Local> {
        database.withTransactionFacade {
            shopDao.deactivateAll()
            shopDao.save(ShopEntity(shop = shop, isActivated = true))
            database.postActivateShop()
        }
        return Result.Success(Unit)
    }

    override fun findTop10ShopsOrderByIsActivated(): Flow<List<Shop>> {
        return shopDao.findTop10ShopsOrderByIsActivated().map { entities ->
            entities.map { it.shop.copy(isActivated = it.isActivated) }
        }
    }

    override suspend fun getActiveShop(): Result<Shop, ConfigurationError> {
        val shop = shopDao.getActivated()
            ?: return Result.Failure(ConfigurationError.ShopSelectionRequired)
        return Result.Success(data = shop.shop.copy(isActivated = true))
    }
}