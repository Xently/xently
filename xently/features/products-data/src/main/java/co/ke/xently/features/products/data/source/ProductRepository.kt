package co.ke.xently.features.products.data.source

import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun save(product: Product): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Product, DataError>>
}
