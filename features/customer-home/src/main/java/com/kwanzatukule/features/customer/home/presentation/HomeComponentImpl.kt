package com.kwanzatukule.features.customer.home.presentation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.libraries.pagination.domain.PagingSource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class HomeComponentImpl(
    context: ComponentContext,
    component: HomeComponent,
    private val repository: com.kwanzatukule.features.customer.home.data.HomeRepository,
) : HomeComponent by component, ComponentContext by context {
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
    override val advert: StateFlow<com.kwanzatukule.features.customer.home.data.Advert?> =
        repository.getAdvert().stateIn(
            scope = componentScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = null,
        )

    override val paginatedCategories: Flow<PagingData<Category>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getCategories(url)
        }
    }.flow.cachedIn(componentScope)

    override val paginatedSuggestedProducts: Flow<PagingData<Product>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getProducts(url ?: "https://localhost/suggested-products")
        }
    }.flow.cachedIn(componentScope)

    override val paginatedFeaturedProducts: Flow<PagingData<Product>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getProducts(url ?: "https://localhost/featured-products")
        }
    }.flow.cachedIn(componentScope)

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    override fun addToOrRemoveFromShoppingCart(product: Product) {
        componentScope.launch {
            repository.addToOrRemoveFromShoppingCart(product)
        }
    }
}