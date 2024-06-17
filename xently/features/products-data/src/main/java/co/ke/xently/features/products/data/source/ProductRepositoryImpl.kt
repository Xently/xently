package co.ke.xently.features.products.data.source

import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.Error
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.domain.error.toProductError
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Singleton
internal class ProductRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProductDatabase,
) : ProductRepository {
    override suspend fun save(product: Product): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toProductError())
        }
    }

    override suspend fun findById(id: Long): Flow<Result<Product, Error>> {
        TODO("Not yet implemented")
    }

    override suspend fun getProducts(
        url: String,
        filters: ProductFilters,
    ): PagedResponse<Product> {
        return httpClient.get(urlString = url) {
            url {
                parameters.run {
                    if (!filters.categories.isNullOrEmpty()) {
                        appendMissing("category", filters.categories.map { it.name })
                    }
                    if (!filters.query.isNullOrBlank()) {
                        set("q", filters.query)
                    }
                }
            }
        }.body<PagedResponse<Product>>().run {
            copy(embedded = mapOf("views" to (embedded.values.firstOrNull() ?: emptyList())))
        }
    }

    override suspend fun deleteProduct(product: Product): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toProductError())
        }
    }
}