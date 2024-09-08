package com.kwanzatukule.features.customer.home.data

import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

@Singleton
class HomeRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    catalogueRepository: CatalogueRepository,
    shoppingCartRepository: ShoppingCartRepository,
) : HomeRepository, CatalogueRepository by catalogueRepository,
    ShoppingCartRepository by shoppingCartRepository {
    override fun getAdvert(): Flow<Advert> {
        return flow {
            val advert = try {
                httpClient.get("https://localhost")
                    .body<Advert>()
            } catch (ex: Exception) {
                coroutineContext.ensureActive()
                Advert(
                    title = "Today's Deal",
                    subtitle = "20% off on all products",
                    headline = "KES. 80",
                    image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                )
            }
            emit(advert)
        }
    }
}
