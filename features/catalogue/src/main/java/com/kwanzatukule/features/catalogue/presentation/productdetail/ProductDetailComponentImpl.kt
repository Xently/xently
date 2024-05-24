package com.kwanzatukule.features.catalogue.presentation.productdetail

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


class ProductDetailComponentImpl(
    context: ComponentContext,
    product: Product,
    component: ProductDetailComponent,
    private val repository: CatalogueRepository,
) : ProductDetailComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _uiState = MutableValue(ProductDetailUiState(product = product))
    override val uiState: Value<ProductDetailUiState> = _uiState

    private val _event = Channel<ProductDetailEvent>()
    override val event: Flow<ProductDetailEvent> = _event.receiveAsFlow()

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }
}