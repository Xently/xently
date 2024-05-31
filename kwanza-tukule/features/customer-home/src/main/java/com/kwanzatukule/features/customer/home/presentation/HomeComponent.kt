package com.kwanzatukule.features.customer.home.presentation

import androidx.paging.PagingData
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

interface HomeComponent {
    val advert: StateFlow<com.kwanzatukule.features.customer.home.data.Advert?> get() = throw NotImplementedError()
    val paginatedCategories: Flow<PagingData<Category>> get() = throw NotImplementedError()
    val paginatedSuggestedProducts: Flow<PagingData<Product>> get() = throw NotImplementedError()
    val paginatedFeaturedProducts: Flow<PagingData<Product>> get() = throw NotImplementedError()
    fun onSignInRequested()
    fun onSignOutRequested()
    fun navigateToCatalogue(category: Category?)
    fun navigateToProductDetail(product: Product)
    fun navigateToShoppingCart()
    fun addToOrRemoveFromShoppingCart(product: Product) {}

    class Fake(
        ad: com.kwanzatukule.features.customer.home.data.Advert?,
        categories: PagingData<Category>,
        suggestedProducts: PagingData<Product>,
        featuredProducts: PagingData<Product>,
    ) : HomeComponent {
        override val advert = MutableStateFlow(ad)
        override val paginatedCategories = flowOf(categories)
        override val paginatedSuggestedProducts = flowOf(suggestedProducts)
        override val paginatedFeaturedProducts = flowOf(featuredProducts)

        override fun onSignInRequested() {}
        override fun onSignOutRequested() {}
        override fun navigateToCatalogue(category: Category?) {}
        override fun navigateToProductDetail(product: Product) {}
        override fun navigateToShoppingCart() {}
    }
}
