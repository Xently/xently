package co.ke.xently.features.products.data.source

import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.Error
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun save(product: Product, images: List<UploadRequest>): Result<Unit, Error>
    suspend fun findById(id: Long): Flow<Result<Product, Error>>
    suspend fun getProducts(url: String, filters: ProductFilters): PagedResponse<Product>
    suspend fun deleteProduct(product: Product): Result<Unit, Error>
}
