package com.kwanzatukule.features.catalogue.data

import co.ke.xently.libraries.pagination.domain.models.PagedResponse
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class CatalogueRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val shoppingCartChecker: ShoppingCartChecker,
) : CatalogueRepository {
    override suspend fun getCategories(url: String?): PagedResponse<Category> {
        val categories = listOf(
            Category(
                name = "Bevarage",
                image = "https://hotpoint.co.ke/media/cache/3b/ac/3bac8cc2023794721ab87177abfb049d.webp",
            ),
            Category(
                name = "Sugar",
                image = "https://hotpoint.co.ke/media/images/HVAC_Clearance_Sale_Apri.width-600.format-webp-lossless.webp",
            ),
            Category(
                name = "Flour",
                image = "https://hotpoint.co.ke/media/images/Generic_KV.width-600.format-webp-lossless.webp",
            ),
            Category(
                name = "Cooking Oil",
                image = "https://hotpoint.co.ke/media/images/BUILT_IN_SALE.width-600.format-webp-lossless.webp",
            ),
            Category(
                name = "Category Name",
                image = "https://hotpoint.co.ke/media/images/tvs_n_audio.width-600.format-webp-lossless.webp",
            ),
            Category(
                name = "Category Name",
                image = "https://hotpoint.co.ke/media/images/fridges_n_freezers.width-600.format-webp-lossless.webp",
            ),
        ).mapIndexed { index, category -> category.copy(id = (index + 1).toLong()) }.shuffled()
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to categories))
        return httpClient.get(url ?: "https://localhost")
            .body()
    }

    override suspend fun getProducts(
        url: String?,
        filters: CatalogueFilters,
    ): PagedResponse<Product> {
        val products = listOf(
            Product(
                name = "Product Name",
                price = 100,
                images = List(3) { "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp" },
            ),
            Product(
                name = "Product with a really long name",
                price = 200,
                images = List(3) { "https://hotpoint.co.ke/media/cache/13/ef/13efddfbcb3709fd76c31ee16e631be6@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 2_080,
                images = List(3) { "https://hotpoint.co.ke/media/cache/15/68/1568cc5249ebf8a84ca6e2cbeb6610e4@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 10_800,
                images = List(3) { "https://hotpoint.co.ke/media/cache/b5/b0/b5b07cd8fa5c962021ee048c6ffffcba@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 280,
                images = List(3) { "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 80,
                images = List(3) { "https://hotpoint.co.ke/media/cache/87/af/87affefd47f37424b57efb0c1e5c34dc@2x.webp" },
            ),
            // Duplicate start
            Product(
                name = "Product Name",
                price = 100,
                images = List(3) { "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 200,
                images = List(3) { "https://hotpoint.co.ke/media/cache/13/ef/13efddfbcb3709fd76c31ee16e631be6@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 2_080,
                images = List(3) { "https://hotpoint.co.ke/media/cache/15/68/1568cc5249ebf8a84ca6e2cbeb6610e4@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 10_800,
                images = List(3) { "https://hotpoint.co.ke/media/cache/b5/b0/b5b07cd8fa5c962021ee048c6ffffcba@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 280,
                images = List(3) { "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp" },
            ),
            Product(
                name = "Product Name",
                price = 80,
                images = List(3) { "https://hotpoint.co.ke/media/cache/87/af/87affefd47f37424b57efb0c1e5c34dc@2x.webp" },
            ),
        ).mapIndexed { index, product ->
            product.copy(
                id = (index + 1).toLong(),
                categories = List(Random.nextInt(10)) {
                    Category(
                        id = (it + 1).toLong(),
                        name = "Category Name $it",
                        image = "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp"
                    )
                },
                description = if ((index + 1) % 2 != 0) null else {
                    """The Material Research Team conducted two studies (quantitative and qualitative) with over 200 participants to understand their perspectives of five different carousel designs. The studies measured their understanding of how to interact with each carousel, their expectations of the number of items in each design, and how they expected carousels to be used.
            
Summary of findings:

Participants thought carousels were a good way to explore many different types of content.
A previewed or squished item strongly indicated that there was more content to swipe through.
Participants expected around 10 items in a carousel that scrolled multiple items at once.
While some contexts were considered better for some carousel designs, all designs were considered similarly usable."""
                },
            )
        }.shuffled()
        delay(Random.nextLong(2_000))
        return PagedResponse(embedded = mapOf("views" to products)).run {
            copy(
                embedded = embedded.mapValues { (_, value) ->
                    value.map { it.copy(inShoppingCart = shoppingCartChecker.containsProduct(it)) }
                },
            )
        }
        return httpClient.get(url ?: "https://localhost") {
            url {
                parameters.run {
                    if (filters.category != null) {
                        appendMissing("category", listOf(filters.category.name))
                    }
                    if (!filters.query.isNullOrBlank()) {
                        set("q", filters.query)
                    }
                }
            }
        }.body()
    }
}