package co.ke.xently.features.products.data.source

import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.ProductFilters
import co.ke.xently.features.products.data.domain.error.ConfigurationError
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.Error
import co.ke.xently.features.products.data.domain.error.Result
import co.ke.xently.features.products.data.domain.error.toError
import co.ke.xently.features.products.data.source.local.ProductDatabase
import co.ke.xently.features.products.data.source.local.ProductEntity
import co.ke.xently.features.stores.data.source.StoreRepository
import co.ke.xently.libraries.data.image.domain.Upload
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.data.image.domain.UriToByteArrayConverter
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.discardRemaining
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlin.let
import kotlin.random.Random
import kotlin.run
import kotlin.time.Duration.Companion.milliseconds
import kotlin.to
import co.ke.xently.features.stores.data.domain.error.Result as StoreResult

@Singleton
internal class ProductRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProductDatabase,
    private val storeRepository: StoreRepository,
    private val converter: UriToByteArrayConverter,
) : ProductRepository {
    private val productDao = database.productDao()

    override suspend fun save(product: Product, images: List<Upload>): Result<Unit, Error> {
        val httpRequestBuilder: HttpRequestBuilder.() -> Unit = {
            contentType(ContentType.Application.Json)
            val body = ProductSaveRequest(
                unitPrice = product.unitPrice,
                name = product.name,
                packCount = product.packCount,
                description = product.description,
                categories = product.categories.toSet(),
            )
            setBody(body)
        }

        try {
            if (product.links["self"]?.href?.isNotBlank() == true) {
                val urlString = product.links["self"]!!.hrefWithoutQueryParamTemplates()
                httpClient.put(urlString, httpRequestBuilder)
            } else {
                val store = when (val result = storeRepository.getActiveStore()) {
                    is StoreResult.Success -> result.data
                    is StoreResult.Failure -> {
                        return Result.Failure(ConfigurationError.valueOf(result.error.name))
                    }
                }
                val urlString = store.links["products"]!!.hrefWithoutQueryParamTemplates()
                httpClient.post(urlString, httpRequestBuilder)
            }.body<Product>().let {
                deleteReplacedImages(images = images, existingImages = product.images)
                uploadNewImages(images = images, urlString = it.links["images"]!!.href)
                saveProductWithUpdatedImages(product = it)
            }
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }

    private suspend fun saveProductWithUpdatedImages(product: Product) {
        coroutineScope {
            launch(NonCancellable) {
                httpClient.get(urlString = product.links["images"]!!.href)
                    .body<PagedResponse<UploadResponse>>().run {
                        val newImages = (embedded.values.firstOrNull() ?: emptyList())
                        productDao.insertAll(ProductEntity(product = product.copy(images = newImages)))
                    }
            }
        }
    }

    private suspend fun uploadNewImages(images: List<Upload>, urlString: String) {
        coroutineScope {
            images.filterIsInstance<UploadRequest>().map { request ->
                async {
                    request.post(
                        client = httpClient,
                        converter = converter,
                        urlString = urlString,
                    )
                }
            }
        }.awaitAll()
    }

    private suspend fun deleteReplacedImages(
        images: List<Upload>,
        existingImages: List<UploadResponse>,
    ) {
        coroutineScope {
            val retainedImageUrls = images.filterIsInstance<UploadResponse>().map { it.url() }
            val imagesToDelete = existingImages.filter { it.url() !in retainedImageUrls }
            imagesToDelete.map { image ->
                async {
                    httpClient.delete(urlString = image.links["self"]!!.href)
                        .discardRemaining()
                }
            }
        }.awaitAll()
    }

    override suspend fun findById(id: Long): Flow<Result<Product, Error>> {
        return productDao.findById(id = id).map { entity ->
            if (entity == null) {
                Result.Failure(DataError.Network.ResourceNotFound)
            } else {
                Result.Success(entity.product)
            }
        }
    }

    override suspend fun getProducts(
        url: String,
        filters: ProductFilters,
    ): PagedResponse<Product> {
        return httpClient.get(urlString = url) {
            url {
                parameters.run {
                    if (!filters.categories.isNullOrEmpty()) {
                        appendMissing("category", filters.categories.map { it.name })
                    }
                    if (!filters.query.isNullOrBlank()) {
                        set("q", filters.query)
                    }
                }
            }
        }.body<PagedResponse<Product>>().run {
            (embedded.values.firstOrNull() ?: emptyList()).let { products ->
                coroutineScope {
                    launch { productDao.insertAll(products.map { ProductEntity(product = it) }) }
                }
                copy(embedded = mapOf("views" to products))
            }
        }
    }

    override suspend fun deleteProduct(product: Product): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }
}