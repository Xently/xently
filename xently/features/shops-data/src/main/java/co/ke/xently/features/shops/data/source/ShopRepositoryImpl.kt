package co.ke.xently.features.shops.data.source

import co.ke.xently.features.merchant.data.domain.Merchant
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.ShopFilters
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.source.local.ShopDatabase
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
) : ShopRepository {
    override suspend fun save(shop: Shop, merchant: Merchant): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.entries.random())
        }
    }

    override suspend fun findById(id: Long): Flow<Result<Shop, DataError>> {
        TODO("Not yet implemented")
    }

    override suspend fun getShops(url: String?, filters: ShopFilters): PagedResponse<Shop> {
        val shops = List(20) {
            Shop(
                id = it + 1L,
                name = "Shop name ${it + 1}",
                slug = "shop-name-${it + 1}",
                onlineShopUrl = "https://example.com",
                links = mapOf(
                    "self" to Link(href = "https://example.com/${it + 1}"),
                    "add-store" to Link(href = "https://example.com/edit"),
                ),
            )
        }
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to shops))
        return httpClient.get(url ?: "https://localhost")
            .body()
    }

    override suspend fun deleteShop(shop: Shop): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.entries.random())
        }
    }

    override suspend fun selectShop(shop: Shop): Result<Unit, DataError.Local> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Local.entries.random())
        }
    }

    override fun findTop10ShopsOrderByIsActivated(): Flow<List<Shop>> {
        return database.shopDao().findTop10ShopsOrderByIsActivated()
            .map { entities ->
                entities.map { it.shop.copy(isActivated = true) }.ifEmpty {
                    List(10) {
                        Shop.DEFAULT.copy(id = it + 1L, isActivated = it == 1)
                    }
                }
            }
    }
}