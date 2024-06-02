package co.ke.xently.features.products.data.source

import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.source.local.ProductDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ProductRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProductDatabase,
) : ProductRepository {
    override suspend fun save(product: Product): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Product, DataError>> {
        TODO("Not yet implemented")
    }
}