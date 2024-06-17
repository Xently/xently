package co.ke.xently.features.shops.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopFilters
import co.ke.xently.features.shops.data.domain.error.ConfigurationError
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Error
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.domain.error.toShopError
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.features.shops.data.source.local.ShopEntity
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Singleton
internal class ShopRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ShopDatabase,
    private val accessControlRepository: AccessControlRepository,
) : ShopRepository {
    private val shopDao = database.shopDao()

    override suspend fun save(shop: Shop, merchant: Merchant): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toShopError())
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

    override suspend fun getShops(url: String?, filters: ShopFilters): PagedResponse<Shop> {
        val urlString =
            url ?: accessControlRepository.getAccessControl().shopsAssociatedWithMyAccountUrl
        return httpClient.get(urlString = urlString)
            .body<PagedResponse<Shop>>().let { pagedResponse ->
                val shops = pagedResponse.embedded.values.firstOrNull() ?: emptyList()
                coroutineScope {
                    launch {
                        val activatedShop = shopDao.getActivated()
                        shopDao.save(
                            shops.map {
                                ShopEntity(it, isActivated = it.id == activatedShop?.id)
                            }
                        )
                    }
                }
                pagedResponse.copy(embedded = mapOf("views" to shops))
            }
    }

    override suspend fun deleteShop(shop: Shop): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toShopError())
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
}