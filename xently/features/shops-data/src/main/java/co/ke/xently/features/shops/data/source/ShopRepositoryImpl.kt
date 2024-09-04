package co.ke.xently.features.shops.data.source

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
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
            if (ex is CancellationException) throw ex
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

    override suspend fun getShops(url: String?, filters: ShopFilters): PagedResponse<Shop> {
        val urlString =
            url ?: accessControlRepository.getAccessControl().shopsAssociatedWithMyAccountUrl
        return httpClient.get(urlString = urlString) {
            url {
                encodedParameters.run {
                    if (!filters.query.isNullOrBlank()) {
                        set("query", filters.query)
                    }
                }
            }
        }.body<PagedResponse<Shop>>().let { pagedResponse ->
            val shops = pagedResponse.embedded.values.firstOrNull() ?: emptyList()
            coroutineScope {
                launch {
                    database.withTransactionFacade {
                        val activatedShop = shopDao.getActivated()
                        if (url == null) {
                            shopDao.deleteAllExceptActivated()
                        }
                        shopDao.save(
                            shops.map {
                                ShopEntity(it, isActivated = it.id == activatedShop?.id)
                            }
                        )
                    }
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