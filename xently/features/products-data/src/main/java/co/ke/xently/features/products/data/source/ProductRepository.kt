package co.ke.xently.features.products.data.source

import androidx.paging.PagingData
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.Error
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.libraries.data.image.domain.Upload
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun save(product: Product, images: List<Upload>): Result<Unit, Error>
    suspend fun findById(id: Long): Flow<Result<Product, Error>>
    fun getProducts(url: String, filters: ProductFilters): Flow<PagingData<Product>>
    suspend fun deleteProduct(product: Product): Result<Unit, Error>
}
