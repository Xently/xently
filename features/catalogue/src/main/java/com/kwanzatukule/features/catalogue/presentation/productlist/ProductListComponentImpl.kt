package com.kwanzatukule.features.catalogue.presentation.productlist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.catalogue.data.CatalogueFilters
import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.libraries.pagination.domain.PagingSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


class ProductListComponentImpl(
    context: ComponentContext,
    category: Category?,
    component: ProductListComponent,
    private val repository: CatalogueRepository,
) : ProductListComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _uiState = MutableValue(ProductListUiState(category = category))
    override val uiState: Value<ProductListUiState> = _uiState

    private val _event = Channel<ProductListEvent>()
    override val event: Flow<ProductListEvent> = _event.receiveAsFlow()

    override val products: Flow<PagingData<Product>> = Pager(
        PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
        )
    ) {
        PagingSource { url ->
            repository.getProducts(
                url = url,
                filters = CatalogueFilters(category = category),
            )
        }
    }.flow.cachedIn(componentScope)

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }
}