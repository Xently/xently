package co.ke.xently.features.customers.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.CustomerFilters
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import co.ke.xently.features.customers.data.source.local.CustomerEntity
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.pagination.data.DataManager
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.RemoteMediator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.fullPath
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CustomerRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: CustomerDatabase,
    private val accessControlRepository: AccessControlRepository,
    private val dispatchersProvider: DispatchersProvider,
) : CustomerRepository {
    private val customerDao = database.customerDao()
    override suspend fun getCustomersUrl(): String {
        return accessControlRepository.getAccessControl().rankingsStatisticsUrl
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getCustomers(
        url: String,
        filters: CustomerFilters,
    ): Flow<PagingData<Customer>> {
        val pagingConfig = PagingConfig(
            pageSize = 20,
//            initialLoadSize = 20,
//            prefetchDistance = 0,
        )

        val urlString = URLBuilder(url).apply {
            encodedParameters.run {
                set("size", pagingConfig.pageSize.toString())
            }
        }.build().fullPath
        val keyManager = LookupKeyManager.URL(url = urlString)

        val dataManager = object : DataManager<Customer> {
            override suspend fun insertAll(lookupKey: String, data: List<Customer>) {
                customerDao.save(
                    data.map { customer ->
                        CustomerEntity(
                            customer = customer,
                            lookupKey = lookupKey,
                        )
                    },
                )
            }

            override suspend fun deleteByLookupKey(lookupKey: String) {
                customerDao.deleteByLookupKey(lookupKey)
            }

            override suspend fun fetchData(url: String?): PagedResponse<Customer> {
                return httpClient.get(urlString = url ?: urlString)
                    .body<PagedResponse<Customer>>()
            }
        }
        val lookupKey = keyManager.getLookupKey()
        return Pager(
            config = pagingConfig,
            remoteMediator = RemoteMediator(
                database = database,
                keyManager = keyManager,
                dataManager = dataManager,
                dispatchersProvider = dispatchersProvider,
            ),
        ) {
            customerDao.getCustomersByLookupKey(lookupKey = lookupKey)
        }.flow.map { pagingData ->
            pagingData.map {
                it.customer
            }
        }
    }
}