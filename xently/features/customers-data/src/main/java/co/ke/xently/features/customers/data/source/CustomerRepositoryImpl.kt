package co.ke.xently.features.customers.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.CustomerFilters
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import co.ke.xently.features.customers.data.source.local.CustomerEntity
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CustomerRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: CustomerDatabase,
    private val accessControlRepository: AccessControlRepository,
) : CustomerRepository {
    private val customerDao = database.customerDao()
    override suspend fun getCustomers(
        url: String?,
        filters: CustomerFilters,
    ): PagedResponse<Customer> {
        val urlString = url ?: accessControlRepository.getAccessControl().rankingsStatisticsUrl
        return httpClient.get(urlString = urlString).body<PagedResponse<Customer>>().run {
            (embedded.values.firstOrNull() ?: emptyList()).let { customers ->
                coroutineScope {
                    launch {
                        database.withTransactionFacade {
                            customerDao.save(customers.map { CustomerEntity(customer = it) })
                        }
                    }
                }
                copy(embedded = mapOf("views" to customers))
            }
        }
    }
}