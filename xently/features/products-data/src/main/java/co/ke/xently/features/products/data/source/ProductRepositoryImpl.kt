package co.ke.xently.features.products.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
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
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.data.image.domain.Upload
import co.ke.xently.libraries.data.image.domain.UploadRequest
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.data.image.domain.UriToByteArrayConverter
import co.ke.xently.libraries.pagination.data.DataManager
import co.ke.xently.libraries.pagination.data.LookupKeyManager
import co.ke.xently.libraries.pagination.data.PagedResponse
import co.ke.xently.libraries.pagination.data.RemoteMediator
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
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.fullPath
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.yield
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.Exception
import kotlin.Long
import kotlin.OptIn
import kotlin.String
import kotlin.Unit
import kotlin.apply
import kotlin.let
import kotlin.random.Random
import kotlin.run
import kotlin.time.Duration.Companion.milliseconds
import co.ke.xently.features.stores.data.domain.error.Result as StoreResult

@Singleton
internal class ProductRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ProductDatabase,
    private val storeRepository: StoreRepository,
    private val converter: UriToByteArrayConverter,
    private val dispatchersProvider: DispatchersProvider,
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
                synonyms = product.synonyms.toSet(),
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
            yield()
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }

    private suspend fun saveProductWithUpdatedImages(product: Product) {
        supervisorScope {
            httpClient.get(urlString = product.links["images"]!!.href)
                .body<PagedResponse<UploadResponse>>().run {
                    val newImages = (embedded.values.firstOrNull() ?: emptyList())
                    productDao.save(ProductEntity(product = product.copy(images = newImages)))
                }
        }
    }

    private suspend fun uploadNewImages(images: List<Upload>, urlString: String) {
        supervisorScope {
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
        supervisorScope {
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

    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(
        url: String,
        filters: ProductFilters,
    ): Flow<PagingData<Product>> {
        val pagingConfig = PagingConfig(
            pageSize = 20,
//            initialLoadSize = 20,
//            prefetchDistance = 0,
        )

        val urlString = URLBuilder(url).apply {
            encodedParameters.run {
                set("size", pagingConfig.pageSize.toString())
                if (!filters.categories.isNullOrEmpty()) {
                    appendMissing("category", filters.categories.map { it.name })
                }
                if (!filters.query.isNullOrBlank()) {
                    set("q", filters.query)
                }
            }
        }.build().fullPath
        val keyManager = LookupKeyManager.URL(url = urlString)

        val dataManager = object : DataManager<Product> {
            override suspend fun insertAll(lookupKey: String, data: List<Product>) {
                productDao.save(
                    data.map { product ->
                        ProductEntity(
                            product = product,
                            lookupKey = lookupKey,
                        )
                    },
                )
            }

            override suspend fun deleteByLookupKey(lookupKey: String) {
                productDao.deleteByLookupKey(lookupKey)
            }

            override suspend fun fetchData(url: String?): PagedResponse<Product> {
                return httpClient.get(urlString = url ?: urlString)
                    .body<PagedResponse<Product>>()
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
            productDao.getProductsByLookupKey(lookupKey = lookupKey)
        }.flow.map { pagingData ->
            pagingData.map {
                it.product
            }
        }
    }

    override suspend fun deleteProduct(product: Product): Result<Unit, Error> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            yield()
            Timber.e(ex)
            return Result.Failure(ex.toError())
        }
    }
}