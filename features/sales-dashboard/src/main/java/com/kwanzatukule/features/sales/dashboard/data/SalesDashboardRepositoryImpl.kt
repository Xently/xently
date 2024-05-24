package com.kwanzatukule.features.sales.dashboard.data

import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem
import io.ktor.client.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Singleton
class SalesDashboardRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: SalesDashboardDatabase,
) : SalesDashboardRepository {
    override fun getSalesDashboardContent(): Flow<List<SalesDashboardItem>> {
        val refreshInterval: Duration = 1.minutes
        suspend fun save(): List<SalesDashboardItem> {
            Timber.i("Refreshing sales dashboard...")
            /*val content = httpClient.get("https://example.com")
                .body<List<SalesDashboardItem>>()*/
            delay(Random.nextLong(100, 2000))
            val content = List(Random.nextInt(5, 20)) {
                SalesDashboardItem(
                    name = "Item $it",
                    actual = Random.nextInt(50_000),
                    target = Random.nextInt(20_000, 50_000),
                    status = if ((it + 1) % 3 == 0) {
                        SalesDashboardItem.Status.AHEAD
                    } else {
                        SalesDashboardItem.Status.SHORT
                    },
                )
            }
            withContext(NonCancellable) {
                database.withTransactionFacade {
                    val entities = content.map { SalesDashboardItemEntity(it) }
                    with(database.salesDashboardItemEntityDao()) {
                        clearEntries()
                        save(entities)
                    }
                }
            }
            return content
        }
        return database.salesDashboardItemEntityDao()
            .getSalesDashboardItems()
            .map { items ->
                items.map { item -> item.item }
            }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    emit(save())
                    Timber.i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }
    }
}