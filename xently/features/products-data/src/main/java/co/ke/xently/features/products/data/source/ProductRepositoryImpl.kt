package co.ke.xently.features.products.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.ProductImage
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

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

    override suspend fun getProducts(
        url: String?,
        filters: ProductFilters,
    ): PagedResponse<Product> {
        val products = listOf(
            Product(
                name = "Bevarage",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ProductImage(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/cache/3b/ac/3bac8cc2023794721ab87177abfb049d.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Sugar",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ProductImage(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/images/HVAC_Clearance_Sale_Apri.width-600.format-webp-lossless.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Flour",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ProductImage(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/images/Generic_KV.width-600.format-webp-lossless.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Cooking Oil",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ProductImage(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/images/BUILT_IN_SALE.width-600.format-webp-lossless.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Product Name",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ProductImage(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/images/tvs_n_audio.width-600.format-webp-lossless.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Category Name",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ProductImage(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/images/fridges_n_freezers.width-600.format-webp-lossless.webp",
                            ),
                        ),
                    )
                },
            ),
        ).mapIndexed { index, product ->
            product.copy(
                id = (index + 1).toLong(),
                categories = List(Random.nextInt(3)) {
                    ProductCategory(
                        name = "Category ${it + 1}",
                    )
                },
            )
        }.shuffled()
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to products))
        return httpClient.get(url ?: "https://localhost")
            .body()
    }
}