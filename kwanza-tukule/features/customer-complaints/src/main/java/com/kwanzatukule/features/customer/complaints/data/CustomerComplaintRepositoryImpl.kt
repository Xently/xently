package com.kwanzatukule.features.customer.complaints.data

import co.ke.xently.libraries.pagination.domain.models.PagedResponse
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.features.customer.complaints.domain.error.DataError
import com.kwanzatukule.features.customer.complaints.domain.error.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

@Singleton
class CustomerComplaintRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: CustomerComplaintDatabase,
) : CustomerComplaintRepository {
    override fun getCustomerComplaints(
        url: String?,
        filter: Filter,
    ): PagedResponse<CustomerComplaint> {
        val content = List(Random.nextInt(5, 20)) {
            CustomerComplaint(
                name = "CustomerComplaint $it",
                email = "customer$it@example.com",
                phone = "+2547${Random.nextLong(10000000, 99999999)}",
                id = (it + 1).toLong(),
            )
        }
        return PagedResponse(embedded = mapOf("views" to content))
        /*val refreshInterval: Duration = 5.seconds
        suspend fun save(): List<CustomerComplaint> {
            Timber.i("Refreshing customers...")
            val content = httpClient.get("https://example.com")
                .body<List<CustomerComplaint>>()
            delay(Random.nextLong(100, 2000))
            withContext(NonCancellable) {
                database.withTransactionFacade {
                    val entities = content.map { CustomerComplaintEntity(it) }
                    with(database.customerDao()) {
                        clear()
                        save(entities)
                    }
                }
            }
            return content
        }
        return database.customerDao()
            .getCustomerComplaints()
            .map { entities -> entities.map { it.customer } }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    emit(save())
                    Timber.i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }*/
    }

    override suspend fun save(customer: CustomerComplaint): Result<Unit, DataError> {
        /*val response = httpClient.post("https://example.com") {
            contentType(ContentType.Application.Json)
            setBody(customer)
        }.body<CustomerComplaint>()*/
        val response = customer
        delay(Random.nextLong(100, 2000))
        return try {
            withContext(Dispatchers.IO) {
                val entity = CustomerComplaintEntity(customer = response)
                database.customerComplaintDao().save(listOf(entity))
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Result.Failure(DataError.Network.UNKNOWN)
        }
    }
}