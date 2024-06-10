package co.ke.xently.features.products.data.source

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.image.domain.ImageResponse
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
    override suspend fun save(product: Product): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.NO_INTERNET)
        }
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
                name = "LG WT1310PB WashTower, 13/10KG - Center Control, AI DD Technology , Smart Pairing™",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = """Space-saving Design
                |Intelligent Wash & Dry
                |Compact size with easy-reach center control panel
                |Dual Inverter Heat Pump™ for energy-efficient drying
                |Smart Pairing™ the drying cycle syncs with the washing cycle selected
                |LG ThinQ™ app assists you to be updated on cycle times, alerts, energy usage, and more
                |Time-saving - the dryer starts to preheat before the end of washing, so drying takes less time
                |Auto Sense AIDD™ technology detects the most suitable wash cycle to handle your clothes with care
                |TurboWash™360 gets your laundry thoroughly cleaned in just 39 minutes without compromising fabric protection*.""".trimMargin(),
                images = List(1) {
                    ImageResponse(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/cache/e2/80/e2806b05cca21801b4b7aa4a27463f45.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Chips Kuku",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ImageResponse(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/cache/e2/80/e2806b05cca21801b4b7aa4a27463f45.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "LG F4V9BDP2EE Front Load Washer Dryer, 12/8KG -AI DD Technology, TurboWash 360, Steam+",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "Dolby Vision™ - See the content in Dazzling Details, whether you want to catch up on the latest content or simply enjoy some time with your favourite immersive games, this TV delivers original content with better detail in the shadows and brighter highlights.",
                images = List(1) {
                    ImageResponse(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/cache/aa/81/aa8159e45be590b88324a8ac033fb4f2.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Hisense 55\" 55A6K UHD Smart TV VIDAA",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "Dolby Vision™ - See the content in Dazzling Details, whether you want to catch up on the latest content or simply enjoy some time with your favourite immersive games, this TV delivers original content with better detail in the shadows and brighter highlights.",
                images = List(1) {
                    ImageResponse(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/cache/1a/62/1a62033e7923d5cf051921856875077e@2x.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Chips Kuku, Bhajia, Smokies, Fish, Yoghurt, Sugar & Chicken",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                images = List(1) {
                    ImageResponse(
                        links = mapOf(
                            "media" to Link(
                                href = "https://hotpoint.co.ke/media/cache/96/3e/963eb5a1c55486a6bcf9ca9cd04ac449.webp",
                            ),
                        ),
                    )
                },
            ),
            Product(
                name = "Bevarage",
                unitPrice = Random.nextInt(200, 5_000).toDouble(),
                description = "A mix of pilau and roasted potatoes garnished with a side of paprika grilled tomatoes.",
                images = List(1) {
                    ImageResponse(
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
                    ImageResponse(
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
                    ImageResponse(
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
                    ImageResponse(
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
                    ImageResponse(
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
                    ImageResponse(
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

    override suspend fun deleteProduct(product: Product): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.NO_INTERNET)
        }
    }
}