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
            description = "Short description about the business/hotel will go here. Lorem ipsum dolor trui loerm ipsum is a repetitive alternative place holder text for design projects.",
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
                description = "Short description about the business/hotel will go here. Lorem ipsum dolor trui loerm ipsum is a repetitive alternative place holder text for design projects.",
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