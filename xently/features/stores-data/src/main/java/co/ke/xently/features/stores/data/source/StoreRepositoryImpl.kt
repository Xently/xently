package co.ke.xently.features.stores.data.source

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.local.StoreDatabase
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.data.image.domain.ImageResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StoreRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: StoreDatabase,
) : StoreRepository {
    override suspend fun save(store: Store): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Flow<Result<Store, DataError>> {
        val store = Store(
            name = "Westlands",
            shop = Shop(name = "Ranalo K'Osewe"),
            description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
        )
        return flowOf(Result.Success(store))
    }

    override fun findActiveStore(): Flow<Store?> {
        return flowOf(
            Store(
                name = "Westlands",
                shop = Shop(
                    name = "Ranalo K'Osewe",
                    onlineShopUrl = "https://picsum.photos/id/237/100/100",
                    id = 1,
                    links = mapOf("add-store" to Link(href = "https://picsum.photos/id/237/100/100")),
                ),
                description = """Short description about the business/hotel will go here.
                |Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
                |
                |Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem.""".trimMargin(),
                services = List(3) {
                    StoreService(name = "Service ${it + 1}")
                },
                categories = List(3) {
                    StoreCategory(name = "Category ${it + 1}")
                },
                images = List(5) {
                    ImageResponse(
                        links = mapOf(
                            "media" to Link(
                                href = "https://picsum.photos/id/237/${100 * (it + 1)}/${100 * (it + 1)}",
                            ),
                        ),
                    )
                },
                openingHours = DayOfWeek.entries.map {
                    OpeningHour(
                        dayOfWeek = it,
                        openTime = Time(7, 0),
                        closeTime = Time(17, 0),
                        open = it.isoDayNumber !in setOf(6, 7),
                    )
                },
            )
        )
    }
}